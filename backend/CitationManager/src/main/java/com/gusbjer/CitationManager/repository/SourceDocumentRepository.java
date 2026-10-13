package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.SourceDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceDocumentRepository extends JpaRepository<SourceDocument, Long> {
}
