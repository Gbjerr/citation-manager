package com.gusbjer.CitationManager.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity for keeping track of registered users.
 */
@Entity
@Table(name = "users")
@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String password;
}