package com.gusbjer.CitationManager.controller;

import com.gusbjer.CitationManager.dto.SimilarityRequestDto;
import com.gusbjer.CitationManager.model.SemanticCitation;
import com.gusbjer.CitationManager.service.SemanticCitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing endpoints for operations on semantic citations;
 */
@RestController
@RequestMapping("/api/semanticcitations")
public class SemanticCitationController {

    @Autowired
    private SemanticCitationService semanticCitationService;

    @PostMapping("/similarity")
    public ResponseEntity<List<SemanticCitation>> getSimilarCitations(@RequestBody SimilarityRequestDto request) {
        List<SemanticCitation> similarCitations = semanticCitationService.getSemanticSimilarCitations(request.getInput());

        return ResponseEntity.ok(similarCitations);
    }

}
