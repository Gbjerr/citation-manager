package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.dto.SemanticMappingProjection;
import com.gusbjer.CitationManager.model.SemanticCitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemanticCitationRepository extends JpaRepository<SemanticCitation, Long> {

    // Query for retrieving the N most semantically similar citations.
    @Query(value = """
            SELECT 
                s.id, 
                ts_rank_cd(s.tsv, to_tsquery('english', :inputText)) AS tsScore, 
                s.embedding <=> CAST(:embedding AS vector) AS cosineScore
            FROM semantic_citations s 
            ORDER BY s.embedding <=> CAST(:embedding AS vector) 
            LIMIT :n
        """, nativeQuery = true)
    List<SemanticMappingProjection> getTopNSemanticallySimilarCitations(@Param("embedding") String embedding, @Param("inputText") String inputText, @Param("n") int n);

    // Query for retrieving the N most textually similar citations.
    @Query(value = """
            SELECT 
                s.id, 
                ts_rank_cd(s.tsv, q) AS tsScore, 
                s.embedding <=> CAST(:embedding AS vector) AS cosineScore
            FROM 
                semantic_citations s, 
                to_tsquery('english', :inputText) q
            WHERE s.tsv @@ q
            ORDER BY tsScore DESC
            LIMIT :n
        """, nativeQuery = true)
    List<SemanticMappingProjection> getTopNTextuallySimilarCitations(@Param("embedding") String embedding, @Param("inputText") String inputText, @Param("n") int n);

}
