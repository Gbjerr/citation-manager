package com.gusbjer.CitationManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for retrieval of semantic citations during similarity search.
 */
@Getter
@Setter
@AllArgsConstructor
public class SemanticMappingDto {
    private Long id;
    private Double tsScore;
    private Double cosineScore;
}
