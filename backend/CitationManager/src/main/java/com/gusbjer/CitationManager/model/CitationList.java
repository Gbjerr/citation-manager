package com.gusbjer.CitationManager.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Citation List entity where each list links to a {@link User}.
 */
@Entity
@Table(name = "citation_lists")
@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CitationList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
