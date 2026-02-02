package com.gusbjer.CitationManager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO for citation data.
 */
@Getter
@Setter
public class CitationDto {
    private String title;
    private String authors;
    private String publisher;
    private Date date;
    private String DOI;
    private String URL;
    private String ISBN;
    private Long citationListId;
}