package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.dto.ChunkSimilarityProjection;
import com.gusbjer.CitationManager.model.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {

    @Query(value = """
            SELECT 
                c.id,
                c.chunk_index AS chunkIndex,
                c.page AS page,
                c.section_path AS sectionPath,
                c.text AS text,
                ts_rank_cd(c.tsv, to_tsquery('english', :inputText)) AS tsScore,
                c.embedding <=> CAST(:inputTextEmbedding AS vector) AS cosineScore,
                s.id AS citationId,
                s.title AS citationTitle
            FROM chunk c
            JOIN source_document sd ON sd.id = c.source_document_id
            JOIN citations s ON s.id = sd.citation_id
            WHERE s.citation_list_id = :citationListId
                    AND c.embedding IS NOT NULL
            ORDER BY c.embedding <=> CAST(:inputTextEmbedding AS vector)
            LIMIT 5
        """, nativeQuery = true)
    List<ChunkSimilarityProjection> findTop5SemanticMatchesByCitationListId(@Param("inputText") String inputText, @Param("inputTextEmbedding") float[] inputTextEmbedding, @Param("citationListId") long citationListId);

    @Query(value = """
        SELECT
            c.id,
            c.chunk_index AS chunkIndex,
            c.page AS page,
            c.section_path AS sectionPath,
            c.text AS text,
            ts_rank_cd(c.tsv, q) AS tsScore,
            c.embedding <=> CAST(:inputTextEmbedding AS vector) AS cosineScore,
            s.id AS citationId,
            s.title AS citationTitle
        FROM chunk c
        JOIN source_document sd ON sd.id = c.source_document_id
        JOIN citations s ON s.id = sd.citation_id
        CROSS JOIN to_tsquery('english', :inputText) q
        WHERE s.citation_list_id = :citationListId
          AND c.embedding IS NOT NULL
          AND c.tsv @@ q
        ORDER BY tsScore DESC
        LIMIT 5
    """, nativeQuery = true)
    List<ChunkSimilarityProjection> findTop5FullTextMatchesByCitationListId(@Param("inputText") String inputText, @Param("inputTextEmbedding") float[] inputTextEmbedding, @Param("citationListId") long citationListId);
}
