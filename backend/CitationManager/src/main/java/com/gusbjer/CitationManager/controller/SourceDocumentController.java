package com.gusbjer.CitationManager.controller;

import com.gusbjer.CitationManager.dto.DocumentIngestRequestDto;
import com.gusbjer.CitationManager.model.Chunk;
import com.gusbjer.CitationManager.model.Citation;
import com.gusbjer.CitationManager.service.CitationService;
import com.gusbjer.CitationManager.service.SourceDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/sourcedocuments")
public class SourceDocumentController {

    @Autowired
    private SourceDocumentService sourceDocumentService;

    @Autowired
    private CitationService citationService;

    @PostMapping("/ingest")
    public ResponseEntity<List<Chunk>> ingestSourceDocument(@RequestBody DocumentIngestRequestDto requestDto) {
        Citation citation = citationService.getCitationById(requestDto.getCitationId());
        if(citation == null) {
            throw new IllegalArgumentException("Citation with ID " + requestDto.getCitationId() + " not found.");
        }

        return ResponseEntity.ok(sourceDocumentService.ingestSourceDocument(requestDto.getFilePath(), citation));
    }
}
