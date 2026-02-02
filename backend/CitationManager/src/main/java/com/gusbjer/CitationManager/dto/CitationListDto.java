package com.gusbjer.CitationManager.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for citation lists.
 */
@Getter
@Setter
public class CitationListDto {
    private Long id;
    private String title;
    private Long userId;
}
