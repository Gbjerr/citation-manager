package com.gusbjer.CitationManager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A source document (PDF) that has been ingested and split into {@link Chunk}s.
 */
@Entity
@Table(name = "source_document")
@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SourceDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    // Ignored in JSON: Citation also references SourceDocument, which would otherwise recurse.
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "citation_id", referencedColumnName = "id")
    private Citation citation;
}
