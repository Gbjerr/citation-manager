package com.gusbjer.CitationManager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentIngestRequestDto {
    private String filePath;
    private String documentTitle;
    private long citationId;
}
