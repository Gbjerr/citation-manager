package com.gusbjer.CitationManager.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@Service
public class SourceDocumentService {
    private static final String GROBID_PROCESS_ENDPOINT = "http://localhost:8070/api/processFulltextDocument";

    public void ingestSourceDocument(String filePath) {
        File file = new File(filePath);
        if(!file.exists()) {
            throw new RuntimeException("File does not exist");
        }

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("input", new FileSystemResource(file));
        body.add("teiCoordinates", "p");
        body.add("teiCoordinates", "figure");
        body.add("teiCoordinates", "title");
        body.add("teiCoordinates", "table");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                GROBID_PROCESS_ENDPOINT,
                request,
                String.class
        );

        if(response.getStatusCode().is2xxSuccessful()) {
            System.out.println("GROBID response: " + response.getBody());

            String grobidResponse = response.getBody().toString();

        } else {
            throw new RuntimeException("Failed to process document with GROBID. Status code: " + response.getStatusCode());
        }
    }
}
