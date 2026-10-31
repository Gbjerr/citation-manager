package com.gusbjer.CitationManager.controller;

import com.gusbjer.CitationManager.dto.DocumentIngestRequestDto;
import com.gusbjer.CitationManager.model.Chunk;
import com.gusbjer.CitationManager.model.Citation;
import com.gusbjer.CitationManager.service.CitationService;
import com.gusbjer.CitationManager.service.SourceDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/sourcedocuments")
public class SourceDocumentController {

    @Autowired
    private SourceDocumentService sourceDocumentService;

    @Autowired
    private CitationService citationService;

    @PostMapping("/ingest")
    public ResponseEntity<List<Chunk>> ingestSourceDocument(@RequestParam MultipartFile file, @RequestParam String documentTitle, @RequestParam long citationId) {
        Citation citation = citationService.getCitationById(citationId);
        if(citation == null) {
            throw new IllegalArgumentException("Citation with ID " + citationId + " not found.");
        }

        return ResponseEntity.ok(sourceDocumentService.ingestSourceDocument(file, citation));
    }
}
