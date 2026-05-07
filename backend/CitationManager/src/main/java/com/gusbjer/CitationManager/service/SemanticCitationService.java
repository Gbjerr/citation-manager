package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.dto.SemanticMappingDto;
import com.gusbjer.CitationManager.model.SemanticCitation;
import com.gusbjer.CitationManager.repository.SemanticCitationRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gusbjer.CitationManager.constants.SimilaritySearchConstants.stopWords;

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
     * Retrieves the most similar citations based on inputQuery query text.
     *
     * @param inputQuery the query text to semantically search or
     * @return the most similar citations
     */
    public List<SemanticCitation> getSimilarCitations(String inputQuery) {
        float[] queryEmbedding = embeddingModel.embed(inputQuery);
        String formattedEmbedding = formatEmbedding(queryEmbedding);

        // Create a string of unique words separated by ' | ', from the input text which excludes stop words.
        String orSeparatedQuery = Stream.of(inputQuery.split("\\s+"))
                .filter(word -> !stopWords.contains(word.toLowerCase()))
                .distinct()
                .collect(Collectors.joining(" | "));

        // The most semantically similar citations given the input.
        List<SemanticMappingDto> semanticResults = semanticCitationRepository.getTopNSemanticallySimilarCitations(formattedEmbedding, orSeparatedQuery, 30)
                .stream()
                .map(projection ->
                        new SemanticMappingDto(projection.getId(), projection.getTsScore(), 1 - projection.getCosineScore()))
                .toList();

        // The most textually similar results given the input.
        List<SemanticMappingDto> textualResults = semanticCitationRepository.getTopNTextuallySimilarCitations(formattedEmbedding, orSeparatedQuery, 30)
                .stream()
                .map(projection ->
                        new SemanticMappingDto(projection.getId(), projection.getTsScore(), 1 - projection.getCosineScore()))
                .toList();

        // MAX/MIN cosine distance scores.
        double maxCosineScore = semanticResults.get(0).getCosineScore();
        double minCosineScore = Stream.concat(semanticResults.stream(), textualResults.stream())
                .mapToDouble(SemanticMappingDto::getCosineScore)
                .min()
                .orElse(0.0);

        // MAX/MIN textual similarity scores.
        double maxTsScore = textualResults.get(0).getTsScore();
        double minTsScore = Stream.concat(semanticResults.stream(), textualResults.stream())
                .mapToDouble(SemanticMappingDto::getTsScore)
                .min()
                .orElse(0.0);

        // Combine the best textual and semantic matches, create a combined rank and sort accordingly.
        List<SemanticCitation> scoreMappings = Stream.concat(semanticResults.stream(), textualResults.stream())
                .map(s -> new SemanticIdScoreMapping(s.getId(), computeCombinedRank(s, minTsScore, maxTsScore, minCosineScore, maxCosineScore)))
                .distinct()
                .sorted((m1, m2) -> Double.compare(m2.rank, m1.rank))
                .limit(5)
                .map(s -> semanticCitationRepository.findById(s.id()).get())
                .collect(Collectors.toList());

        return scoreMappings;
    }

    /**
     * Computes a combined rank based on cosine similarity and textual similarity score.
     */
    private double computeCombinedRank(SemanticMappingDto s, double minTsScore, double maxTsScore, double minCosineScore, double maxCosineScore) {
        double normalizedCosine = ((s.getCosineScore() - minCosineScore) / (maxCosineScore - minCosineScore));
        double normalizedTsScore = ((s.getTsScore() - minTsScore) / (maxTsScore - minTsScore));
        return normalizedCosine * 0.7 + normalizedTsScore * 0.3;
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

    record SemanticIdScoreMapping(Long id, double rank) {
    }
}
