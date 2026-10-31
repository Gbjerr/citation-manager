package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.model.Chunk;
import com.gusbjer.CitationManager.model.Citation;
import com.gusbjer.CitationManager.model.SourceDocument;
import com.gusbjer.CitationManager.repository.SourceDocumentRepository;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

@Service
public class SourceDocumentService {
    private static final String GROBID_PROCESS_PATH = "/api/processFulltextDocument";

    @Value("${grobid.base-url}")
    private String grobidBaseUrl;

    private static final String TEI_COORDINATES_PARAM = "teiCoordinates";
    private static final String[] TEI_COORDINATES_VALUES = {"p", "figure", "title", "table"};

    private static final String BODY_TAG = "body";
    private static final String HEAD_TAG = "head";
    private static final String PARAGRAPH_TAG = "p";
    private static final String REF_TAG = "ref";
    private static final String COORDS_ATTR = "coords";
    private static final String SECTION_ID_ATTR = "n";
    private static final int MAX_ABSTRACT_LEN = 2000;

    @Autowired
    private SourceDocumentRepository sourceDocumentRepository;

    @Autowired
    private ChunkService chunkService;

    @Autowired
    private OllamaEmbeddingModel embeddingModel;

    public List<Chunk> ingestSourceDocument(String filePath, Citation citation) {
        File file = new File(filePath);
        if(!file.exists()) {
            throw new RuntimeException("File does not exist");
        }

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("input", new FileSystemResource(file));
        for(String teiCoordinate : TEI_COORDINATES_VALUES) {
            body.add(TEI_COORDINATES_PARAM, teiCoordinate);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                grobidBaseUrl + GROBID_PROCESS_PATH,
                request,
                String.class
        );

        if(response.getStatusCode().is2xxSuccessful()) {

            if(response.getBody() != null) {
                String grobidResponse = response.getBody();
                List<Chunk> chunks = parseGrobidResponse(grobidResponse, filePath, citation);
                SourceDocument sourceDocument = chunks.isEmpty() ? null : chunks.get(0).getSourceDocument();

                if(sourceDocument != null) {
                    sourceDocumentRepository.save(sourceDocument);
                    chunkService.saveAll(chunks);

                    return chunks;
                }
            }

        } else {
            throw new RuntimeException("Failed to process document with GROBID. Status code: " + response.getStatusCode());
        }
        return Collections.emptyList();
    }

    private List<Chunk> parseGrobidResponse(String grobidResponse, String filePath, Citation citation) {

        List<Chunk> chunks = new ArrayList<>();

        HashMap<String, String> sectionIdentifiers = new HashMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(grobidResponse.getBytes()));

            SourceDocument sourceDocument = SourceDocument.builder()
                    .location(filePath)
                    .citation(citation)
                    .build();

            Element body = (Element) doc.getElementsByTagName(BODY_TAG).item(0);

            NodeList bodyChildren = body.getChildNodes();
            for (int i = 0; i < bodyChildren.getLength(); i++) {
                Node chunkNode = bodyChildren.item(i);

                if (chunkNode.getNodeType() == Node.ELEMENT_NODE) {
                    ChunkData chunkData = extractChunkData((Element) chunkNode, sectionIdentifiers);
                    if(chunkData == null) continue;

                    float[] vectorEmbedding = createVectorEmbedding(chunkData.sectionPath, chunkData.text, embeddingModel);

                    Chunk chunk = Chunk.builder()
                            .sectionIdentifier(chunkData.sectionIdentifier)
                            .sectionTitle(chunkData.sectionTitle)
                            .sectionPath(chunkData.sectionPath)
                            .text(chunkData.text)
                            .page(chunkData.page > 0 ? chunkData.page : null)
                            .embedding(vectorEmbedding)
                            .chunkIndex(chunks.size())
                            .sourceDocument(sourceDocument)
                            .build();

                    chunks.add(chunk);
                }
            }

            return chunks;


        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GROBID response", e);
        }
    }

    private static float[] createVectorEmbedding(String sectionPath, String sectionText, OllamaEmbeddingModel embeddingModel) {
        StringBuilder inputBuilder = new StringBuilder();
        if(sectionPath != null) {
            inputBuilder.append("Section Path: ").append(sectionPath).append("\n\n");
        }
        inputBuilder.append("Section Text: ").append(sectionText);

        String inputText = inputBuilder.toString();
        float[] embedding = embeddingModel.embed(
                inputText.length() > MAX_ABSTRACT_LEN
                        ? inputText.substring(0, MAX_ABSTRACT_LEN)
                        : inputText
        );

        return embedding;
    }

    private ChunkData extractChunkData(Element chunkNode, HashMap<String, String> sectionIdentifiers) {

        String sectionIdentifier = null;
        String sectionTitle = null;

        NodeList childNodes = chunkNode.getElementsByTagName(HEAD_TAG);
        // Extract section identifier and title from the <head> element
        if(childNodes.getLength() > 0) {
            Element headElement = (Element) childNodes.item(0);
            Attr attribute = headElement.getAttributeNode(SECTION_ID_ATTR);

            sectionIdentifier = attribute != null ? attribute.getValue() : null;
            sectionTitle = headElement.getTextContent();
        }

        // Strip a trailing '.' (e.g. "1.2." -> "1.2") before computing the parent path
        if(sectionIdentifier != null && sectionIdentifier.endsWith(".")) {
            sectionIdentifier = sectionIdentifier.substring(0, sectionIdentifier.length() - 1);
        }

        NodeList paragraphNodes = chunkNode.getElementsByTagName(PARAGRAPH_TAG);

        String sectionText = extractSectionText(paragraphNodes);
        int pageNumber = extractPageNumber(paragraphNodes);
        String sectionPath = extractSectionPath(sectionIdentifier, sectionTitle, sectionIdentifiers);

        if(sectionIdentifier != null && !sectionIdentifier.isBlank()) {
            sectionIdentifiers.put(sectionIdentifier, sectionPath);
        }

        return sectionText.isEmpty()
                ? null
                : new ChunkData(sectionIdentifier, sectionTitle, sectionPath, sectionText.trim(), pageNumber);
    }

    private int extractPageNumber(NodeList paragraphNodes) {
        int pageNumber = -1;
        if(paragraphNodes.getLength() > 0) {
            Element firstParagraph = (Element) paragraphNodes.item(0);
            String pageAttr = firstParagraph.getAttribute(COORDS_ATTR);
            if(!pageAttr.isEmpty() && Character.isDigit(pageAttr.charAt(0))) {
                String pageStr = pageAttr.split(",")[0];
                pageNumber = Integer.parseInt(pageStr);
            }
        }

        return pageNumber;
    }

    private String extractSectionText(NodeList paragraphNodes) {

        StringBuilder sectionTextBuilder = new StringBuilder();
        for(int j = 0; j < paragraphNodes.getLength(); j++) {
            Element paragraphElement = (Element) paragraphNodes.item(j);
            NodeList refChildNodes = paragraphElement.getElementsByTagName(REF_TAG);

            // Remove all <ref> child elements from the paragraph
            while(refChildNodes.getLength() > 0) {
                Element refChildElement = (Element) refChildNodes.item(0);
                paragraphElement.removeChild(refChildElement);
            }

            String paragraphText = paragraphElement.getTextContent();
            sectionTextBuilder.append(paragraphText.trim()).append(System.lineSeparator());
        }

        return sectionTextBuilder.toString();
    }

    private String extractSectionPath(String sectionIdentifier, String sectionTitle, HashMap<String, String> sectionIdentifiers) {
        String sectionParent = sectionIdentifier != null && sectionIdentifier.contains(".")
                ? sectionIdentifier.substring(0, sectionIdentifier.lastIndexOf('.'))
                : null;

        String sectionParentPath = sectionParent != null
                ? sectionIdentifiers.get(sectionParent)
                : null;

        String sectionPath = sectionParentPath != null
                ? sectionParentPath + " > " + sectionTitle
                : sectionTitle;

        return sectionPath;
    }

    private record ChunkData(String sectionIdentifier, String sectionTitle, String sectionPath, String text, Integer page) {
    }
}
