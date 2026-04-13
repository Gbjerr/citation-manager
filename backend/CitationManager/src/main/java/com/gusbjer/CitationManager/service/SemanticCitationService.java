package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.model.SemanticCitation;
import com.gusbjer.CitationManager.repository.SemanticCitationRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling retrieval of semantic citations.
 */
@Service
public class SemanticCitationService {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private SemanticCitationRepository semanticCitationRepository;

    /**
     * Retrieves the most semantically similar citations based on inputQuery query text.
     *
     * @param inputQuery the query text to semantically search or
     * @return the most semantically similar citations
     */
    public List<SemanticCitation> getSemanticSimilarCitations(String inputQuery) {
        float[] queryEmbedding = embeddingModel.embed(inputQuery);
        String formattedEmbedding = formatEmbedding(queryEmbedding);

        return semanticCitationRepository.getTopThreeSimilarCitations(formattedEmbedding);
    }

    /**
     * Formats embedding array to a string representation that suits an SQL query for vector similarity search.
     */
    private String formatEmbedding(float[] queryEmbedding) {
        StringBuilder formattedEmbeddingBuilder = new StringBuilder();
        formattedEmbeddingBuilder.append("[");
        for(int i = 0; i < queryEmbedding.length; i++) {
            formattedEmbeddingBuilder.append(queryEmbedding[i]);
            if(i != queryEmbedding.length - 1) {
                formattedEmbeddingBuilder.append(", ");
            }
        }
        formattedEmbeddingBuilder.append("]");

        return formattedEmbeddingBuilder.toString();
    }
}
