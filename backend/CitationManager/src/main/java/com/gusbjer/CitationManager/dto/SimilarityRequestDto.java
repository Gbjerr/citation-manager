package com.gusbjer.CitationManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO describing parameters for similarity search.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SimilarityRequestDto {
    private String input;
}

