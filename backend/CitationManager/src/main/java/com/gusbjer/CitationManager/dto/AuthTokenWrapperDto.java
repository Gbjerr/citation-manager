package com.gusbjer.CitationManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Wrapper DTO for access and refresh tokens.
 */
@Getter @Setter @AllArgsConstructor @Builder
public class AuthTokenWrapperDto {
    private String accessToken;
    private String refreshToken;
}
