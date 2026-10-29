package com.gusbjer.CitationManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

/**
 * A section-aware piece of text extracted from a {@link SourceDocument}, with its embedding vector.
 */
@Entity
@Table(name = "chunk")
@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Chunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_document_id", nullable = false)
    private SourceDocument sourceDocument;

    // Position of the chunk within the source document, starting at 0.
    private int chunkIndex;

    // PDF page (1-based) where the chunk starts, or null if no coordinates.
    private Integer page;

    private String sectionTitle;
    private String sectionIdentifier;

    // Heading path including parent sections, e.g. "2 Methods > 2.3 Data".
    private String sectionPath;

    private String text;

    // pgvector literal, e.g. "[0.1, 0.2, ...]"
    @Column(name = "embedding", columnDefinition = "vector(768)")
    private float[] embedding;
}
