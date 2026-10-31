package com.gusbjer.CitationManager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChunkSimilarityDto {
    private long chunkId;
    private Integer chunkIndex;
    private Integer page;
    private String sectionPath;
    private String text;
    private long citationId;
    private String citationTitle;
    private double tsScore;
    private double cosineScore;
    private double rankScore;
}
