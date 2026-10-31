package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.dto.ChunkSimilarityDto;
import com.gusbjer.CitationManager.dto.ChunkSimilarityProjection;
import com.gusbjer.CitationManager.model.Chunk;
import com.gusbjer.CitationManager.model.CitationList;
import com.gusbjer.CitationManager.repository.ChunkRepository;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gusbjer.CitationManager.constants.SimilaritySearchConstants.stopWords;
import static java.util.stream.Collectors.*;

@Service
public class ChunkService {

    @Autowired
    private ChunkRepository chunkRepository;

    @Autowired
    private CitationListService citationListService;

    @Autowired
    private OllamaEmbeddingModel embeddingModel;

    public List<Chunk> saveAll(List<Chunk> chunks) {
        return chunkRepository.saveAll(chunks);
    }

    public List<ChunkSimilarityDto> getMostSemanticallySimilarChunks(String inputText, float[] inputTextEmbedding, long citationListId) {
        CitationList citationList = citationListService.getCitationListById(citationListId);
        if (citationList == null) {
            throw new IllegalArgumentException("CitationList with ID " + citationListId + " not found.");
        }

        return chunkRepository.findTop5SemanticMatchesByCitationListId(inputText, inputTextEmbedding, citationListId)
                .stream()
                .map(ChunkSimilarityProjection::toDto)
                .collect(toList());
    }

    private List<ChunkSimilarityDto> getMostTextuallySimilarChunks(String inputQuery, float[] inputTextEmbedding, long citationListId) {
        CitationList citationList = citationListService.getCitationListById(citationListId);
        if (citationList == null) {
            throw new IllegalArgumentException("CitationList with ID " + citationListId + " not found.");
        }

        return chunkRepository.findTop5FullTextMatchesByCitationListId(inputQuery, inputTextEmbedding, citationListId)
                .stream()
                .map(ChunkSimilarityProjection::toDto)
                .collect(toList());
    }

    public List<ChunkSimilarityDto> getSimilarCitations(String inputQuery, long citationListId) {

        // Create a string of unique words separated by ' | ', from the input text which excludes stop words.
        String orSeparatedQuery = Stream.of(inputQuery.split("\\s+"))
                .filter(word -> !stopWords.contains(word.toLowerCase()))
                .distinct()
                .collect(Collectors.joining(" | "));

        if (orSeparatedQuery.isBlank()) {
            return List.of();
        }

        // Embed the original natural-language input, not the OR-query.
        float[] inputTextEmbedding = embeddingModel.embed(inputQuery);

        List<ChunkSimilarityDto> semanticResults = getMostSemanticallySimilarChunks(orSeparatedQuery, inputTextEmbedding, citationListId);
        List<ChunkSimilarityDto> textualResults = getMostTextuallySimilarChunks(orSeparatedQuery, inputTextEmbedding, citationListId);

        // Deduplicate by chunk id (keeps first occurrence).
        List<ChunkSimilarityDto> combinedResults = new ArrayList<>(Stream.concat(semanticResults.stream(), textualResults.stream())
                .collect(toMap(ChunkSimilarityDto::getChunkId, csd -> csd, (a, b) -> a, LinkedHashMap::new))
                .values());

        if (combinedResults.isEmpty()) {
            return List.of();
        }

        // Min/max over the combined candidate set.
        DoubleSummaryStatistics cosineStats = combinedResults.stream().mapToDouble(ChunkSimilarityDto::getCosineScore).summaryStatistics();
        DoubleSummaryStatistics tsStats = combinedResults.stream().mapToDouble(ChunkSimilarityDto::getTsScore).summaryStatistics();

        combinedResults.forEach(csd -> csd.setRankScore(computeCombinedRank(csd, tsStats, cosineStats)));

        return combinedResults.stream()
                .sorted(Comparator.comparingDouble(ChunkSimilarityDto::getRankScore).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    private double computeCombinedRank(ChunkSimilarityDto csd, DoubleSummaryStatistics tsStats, DoubleSummaryStatistics cosineStats) {
        double cosineRange = cosineStats.getMax() - cosineStats.getMin();
        double tsRange = tsStats.getMax() - tsStats.getMin();

        // Cosine *distance*: lower is better, so invert. Guard against zero range (division by zero -> NaN/Infinity).
        double normalizedCosine = cosineRange == 0 ? 1.0 : (cosineStats.getMax() - csd.getCosineScore()) / cosineRange;
        double normalizedTsScore = tsRange == 0 ? 0.0 : (csd.getTsScore() - tsStats.getMin()) / tsRange;

        return normalizedCosine * 0.7 + normalizedTsScore * 0.3;
    }
}
