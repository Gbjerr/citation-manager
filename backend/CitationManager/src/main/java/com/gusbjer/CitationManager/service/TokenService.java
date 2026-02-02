package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.model.Token;
import com.gusbjer.CitationManager.model.User;
import com.gusbjer.CitationManager.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Token}s within the system.
 */
@Service
@RequiredArgsConstructor
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    public Token save(String refreshToken, User user) {
        Token token = Token.builder()
                .token(refreshToken)
                .user(user)
                .build();
        return tokenRepository.save(token);
    }

    public Token updateToken(Token token) {
        return tokenRepository.save(token);
    }

    public Token findByToken(String tokenValue) {
        return tokenRepository.findAll().stream()
                .filter(token -> token.getToken().equals(tokenValue))
                .findFirst()
                .orElse(null);
    }

}
