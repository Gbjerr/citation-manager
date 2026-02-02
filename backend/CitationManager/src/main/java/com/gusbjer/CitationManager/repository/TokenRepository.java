package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
