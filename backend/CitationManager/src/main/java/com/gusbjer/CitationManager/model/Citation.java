package com.gusbjer.CitationManager.model;
import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a citation entity containing bibliographic information. Each citation is associated with a
 * {@link CitationList}.
 */
@Entity
@Table(name = "citations")
@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Citation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String authors;
    private String publisher;
    private Date date;
    private String DOI;
    private String URL;
    private String ISBN;
    private int position;

    @ManyToOne
    @JoinColumn(name = "citation_list_id", referencedColumnName = "id")
    private CitationList citationList;

    @OneToOne
    @JoinColumn(name = "source_document_id", referencedColumnName = "id")
    private SourceDocument sourceDocument;
}