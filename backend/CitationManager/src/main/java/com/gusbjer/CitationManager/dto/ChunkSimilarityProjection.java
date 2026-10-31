package com.gusbjer.CitationManager.dto;

public interface ChunkSimilarityProjection {
    Long getId();
    Integer getChunkIndex();
    Integer getPage();
    String getSectionPath();
    String getText();
    Double getTsScore();
    Double getCosineScore();
    Long getCitationId();
    String getCitationTitle();

    static ChunkSimilarityDto toDto(ChunkSimilarityProjection projection) {
        ChunkSimilarityDto dto = new ChunkSimilarityDto();
        dto.setChunkId(projection.getId());
        dto.setChunkIndex(projection.getChunkIndex());
        dto.setPage(projection.getPage());
        dto.setSectionPath(projection.getSectionPath());
        dto.setText(projection.getText());
        dto.setTsScore(projection.getTsScore());
        dto.setCosineScore(projection.getCosineScore());
        dto.setCitationId(projection.getCitationId());
        dto.setCitationTitle(projection.getCitationTitle());
        return dto;
    }
}
