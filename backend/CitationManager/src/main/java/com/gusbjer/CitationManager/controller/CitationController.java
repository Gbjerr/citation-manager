package com.gusbjer.CitationManager.controller;

import com.gusbjer.CitationManager.dto.CitationDto;
import com.gusbjer.CitationManager.model.Citation;
import com.gusbjer.CitationManager.model.CitationList;
import com.gusbjer.CitationManager.service.CitationListService;
import com.gusbjer.CitationManager.service.CitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes endpoints for saving, retrieving, deleting and updating {@link Citation}s.
 */
@RestController
@RequestMapping("/api/citations")
public class CitationController {
    @Autowired
    private CitationService citationService;

    @Autowired
    private CitationListService citationListService;

    @GetMapping("/{id}")
    public ResponseEntity<Citation> getCitationById(@PathVariable("id") Long id) {
        Citation citation = citationService.getCitationById(id);
        if(citation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(citation);
    }

    @GetMapping("/")
    public ResponseEntity<List<Citation>> getAllCitations() {
        List<Citation> citations = citationService.getAllCitations();
        if(citations == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(citations);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Citation> createCitation(@RequestBody CitationDto citationDto) {
        CitationList citationList = citationListService.getCitationListById(citationDto.getCitationListId());

        Citation citation = Citation.builder()
                .title(citationDto.getTitle())
                .authors(citationDto.getAuthors())
                .publisher(citationDto.getPublisher())
                .date(citationDto.getDate())
                .DOI(citationDto.getDOI())
                .URL(citationDto.getURL())
                .ISBN(citationDto.getISBN())
                .citationList(citationList)
                .build();
        Citation createdCitation = citationService.saveCitation(citation);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCitation);
    }

    @GetMapping("/by-citationlist/{citationListId}")
    public ResponseEntity<List<Citation>> getCitationsByCitationListId(@PathVariable Long citationListId) {
        List<Citation> citations = citationService.getCitationsByCitationListId(citationListId);
        if (citations == null || citations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(citations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Citation> updateCitation(@PathVariable Long id, @RequestBody CitationDto citationDto) {
        Citation existingCitation = citationService.getCitationById(id);
        if(existingCitation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        existingCitation.setId(id);
        existingCitation.setTitle(citationDto.getTitle());
        existingCitation.setAuthors(citationDto.getAuthors());
        existingCitation.setPublisher(citationDto.getPublisher());
        existingCitation.setDate(citationDto.getDate());
        existingCitation.setDOI(citationDto.getDOI());
        existingCitation.setURL(citationDto.getURL());
        existingCitation.setISBN(citationDto.getISBN());
        Citation updatedCitation = citationService.saveCitation(existingCitation);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCitation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCitation(@PathVariable Long id) {
        Citation deletedCitation = citationService.deleteCitationById(id);
        if(deletedCitation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
