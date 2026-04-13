package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.SemanticCitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemanticCitationRepository extends JpaRepository<SemanticCitation, Long> {

    // Query for retrieving the 3 most similar citations to the citation represented by the input embedding.
    @Query(value = "SELECT * FROM semantic_citations s ORDER BY s.embedding <=> CAST(:embedding AS vector) LIMIT 3", nativeQuery = true)
    List<SemanticCitation> getTopThreeSimilarCitations(String embedding);
}
