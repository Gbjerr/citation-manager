package com.gusbjer.CitationManager.model;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Token entity which links to {@link User}s for authorization.
 */
@Entity
@Table(name = "tokens")
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String token;
    private boolean expired;
    private boolean revoked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
