package com.gusbjer.CitationManager.dto;

public interface SemanticMappingProjection {
    Long getId();
    Double getTsScore();
    Double getCosineScore();
}
