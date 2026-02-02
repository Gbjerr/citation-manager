package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.Citation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CitationRepository extends JpaRepository<Citation, Long> {
    List<Citation> findByCitationListId(Long citationListId);
}
