package org.loader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Imports citation data from a dataset in JSON format and inserts into the semantic citation DB table.
 */
public class CitationsImporter {
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/citation_db";
    private static final String OLLAMA_URL = "http://localhost:11435";
    // Database credentials, replace accordingly.
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PWD = "postgres";
    // Location of data set.
    private static final String DATASET_PATH = "C:\\citations_dataset\\dblp.json";

    private static final String INSERT_CITATION_SQL = "INSERT INTO semantic_citations (title, publisher, date, doi, authors, abstract, embedding) VALUES (?, ?, ?, ?, ?, ?, ?::vector)";
    private static final String DROP_HNSW_INDEX_SQL = "DROP INDEX IF EXISTS semantic_citations_embedding_idx";
    private static final String DROP_TSV_INDEX_SQL = "DROP INDEX IF EXISTS semantic_citations_tsv_idx";
    private static final String CREATE_HNSW_INDEX_SQL = """
            CREATE INDEX ON semantic_citations
            USING hnsw (embedding vector_cosine_ops)
            WITH (m = 16, ef_construction = 64)
        """;
    private static final String CREATE_TSV_INDEX_SQL = """
            CREATE INDEX semantic_citations_tsv_idx
            ON semantic_citations
            USING GIN (tsv);
        """;
    private static final String SET_MAINTENANCE_MEM_SQL = "SET maintenance_work_mem TO '1GB'";
    private static final String RESET_MAINTENANCE_MEM_SQL = "RESET maintenance_work_mem";

    private static final int MAX_SQL_STATEMENTS = 200000;
    private static final int MAX_ABSTRACT_LEN = 2000;
    private static final int BATCH_SIZE = 1000;

    public static void main(String[] args) throws IOException, SQLException {

        Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PWD);

        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper om = new ObjectMapper(jsonFactory);

        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(OLLAMA_URL)
                .modelName("nomic-embed-text")
                .build();

        try(JsonParser parser = jsonFactory.createParser(new File(DATASET_PATH))) {

            if(parser.nextToken() != JsonToken.START_ARRAY) {
                throw new RuntimeException("Strange format of dataset file, interrupting...");
            }

            // Drop indexes before loading dataset.
            conn.createStatement().execute(DROP_HNSW_INDEX_SQL);
            conn.createStatement().execute(DROP_TSV_INDEX_SQL);

            PreparedStatement insertCitationStmt = conn.prepareStatement(INSERT_CITATION_SQL);

            int insertStmtCount = 0;
            while(insertStmtCount <= MAX_SQL_STATEMENTS && parser.nextToken() == JsonToken.START_OBJECT) {
                JsonNode node = om.readTree(parser);

                JSONObject jsonObject = new JSONObject(node.toString());
                String docType = jsonObject.optString("doc_type", "");
                String doi = jsonObject.optString("doi", "");
                JSONObject indexedAbstract = jsonObject.optJSONObject("indexed_abstract");
                // Only consider journals with DOI and Abstract.
                if("Journal".equals(docType)
                        && indexedAbstract != null
                        && indexedAbstract.getInt("IndexLength") > 5
                        && !doi.isBlank()) {

                    // Extract fields.
                    String title = jsonObject.optString("title", "");
                    String publisher = jsonObject.optString("publisher", "");
                    String year = jsonObject.optString("year", "");
                    String commaSeparatedAuthors = unpackAuthors(jsonObject.optJSONArray("authors"));
                    String abstractText = unpackAbstractText(indexedAbstract);
                    String vectorEmbedding = createVectorEmbedding(title, abstractText, embeddingModel);

                    // Add citation fields here.
                    insertCitationStmt.setString(1, title);
                    insertCitationStmt.setString(2, publisher);
                    if (!year.isEmpty()) {
                        // Dataset only provides year for the issued date.
                        insertCitationStmt.setDate(3, Date.valueOf(year + "-01-01"));
                    } else {
                        insertCitationStmt.setNull(3, java.sql.Types.DATE);
                    }
                    insertCitationStmt.setString(4, doi);
                    insertCitationStmt.setString(5, commaSeparatedAuthors);
                    insertCitationStmt.setString(6, abstractText);
                    insertCitationStmt.setString(7, vectorEmbedding);

                    insertCitationStmt.addBatch();
                    if(insertStmtCount % BATCH_SIZE == 0) {
                        insertCitationStmt.executeBatch();
                    }
                    insertStmtCount++;
                }

                parser.skipChildren();
            }

            conn.createStatement().execute(SET_MAINTENANCE_MEM_SQL);

            // Create indexes after loading dataset.
            conn.createStatement().execute(CREATE_HNSW_INDEX_SQL);
            conn.createStatement().execute(CREATE_TSV_INDEX_SQL);

            conn.createStatement().execute(RESET_MAINTENANCE_MEM_SQL);
        }
    }

    /**
     * Creates the vector embedding using a given embedding model, citation title and abstract text.
     *
     * @param title title of citation.
     * @param abstractText abstract text of citation.
     * @param embeddingModel the given embedding model.
     * @return the vector embedding.
     */
    private static String createVectorEmbedding(String title, String abstractText, OllamaEmbeddingModel embeddingModel) {
        String inputText = "Title: " + title + "\n\n" + "Abstract: " + abstractText;
        Response<Embedding> e = embeddingModel.embed(
                inputText.length() > MAX_ABSTRACT_LEN
                        ? inputText.substring(0, MAX_ABSTRACT_LEN)
                        : inputText
        );

        List<Float> vectorList = e.content().vectorAsList();
        return vectorList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Unpacks the authors to a comma separated string.
     *
     * @param authorsArr the JSON array to extract the authors from.
     * @return the unpacked authors.
     */
    private static String unpackAuthors(JSONArray authorsArr) {
        return authorsArr == null
                ? ""
                : authorsArr.toList().stream()
                  .map(obj -> (String) ((Map<String, ?>) obj).get("name"))
                  .collect(Collectors.joining(", "));
    }

    /**
     * Unpacks the abstract text from an indexed format.
     *
     * @param indexedAbstract the json object which holds the inverted abstract.
     * @return the unpacked abstract text.
     */
    private static String unpackAbstractText(JSONObject indexedAbstract) {
        // Populate indexed tokens with empty strings.
        int invertedAbstractLen = indexedAbstract.getInt("IndexLength");
        List<String> invertedAbstractTokens = new ArrayList<>();
        IntStream.range(0, invertedAbstractLen).forEach(i -> invertedAbstractTokens.add(""));

        // Place tokens.
        JSONObject invertedAbstractObj = indexedAbstract.getJSONObject("InvertedIndex");
        invertedAbstractObj.keys().forEachRemaining(key -> {
            JSONArray positions = invertedAbstractObj.getJSONArray(key);
            for(int i = 0; i < positions.length(); i++) {
                int pos = positions.getInt(i);
                invertedAbstractTokens.set(pos, key);
            }
        });

        return String.join(" ", invertedAbstractTokens);
    }

}