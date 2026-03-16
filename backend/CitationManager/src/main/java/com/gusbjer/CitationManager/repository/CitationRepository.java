package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.Citation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CitationRepository extends JpaRepository<Citation, Long> {
    // Custom query to retrieve citation in order of their position.
    @Query("SELECT c FROM Citation c WHERE c.citationList.id = :citationListId ORDER BY c.position ASC")
    List<Citation> findByCitationListId(Long citationListId);
}
