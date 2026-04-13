package com.gusbjer.CitationManager.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Represents citations purposed for semantic search with its embedding vector stored as a string.
 */
@Entity
@Table(name = "semantic_citations")
@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SemanticCitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(name = "abstract")
    private String abstractText;
    private String publisher;
    private String doi;
    private String authors;
    private Date date;

    @Column(name = "embedding", columnDefinition = "vector(768)")
    private String embedding;
}
