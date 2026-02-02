package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.model.Citation;
import com.gusbjer.CitationManager.repository.CitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link Citation}s in the system.
 */
@Service
public class CitationService {
    @Autowired
    private CitationRepository citationRepository;

    public Citation getCitationById(Long id) {
        Optional<Citation> optCitation = citationRepository.findById(id);
        return optCitation.orElse(null);
    }

    public Citation saveCitation(Citation citation) {
        return citationRepository.save(citation);
    }

    public List<Citation> getAllCitations() {
        return citationRepository.findAll();
    }

    public List<Citation> getCitationsByCitationListId(Long citationListId) {
        return citationRepository.findByCitationListId(citationListId);
    }

    public Citation deleteCitationById(Long id) {
        Optional<Citation> citationOpt = citationRepository.findById(id);

        if(citationOpt.isEmpty()) {
            return null;
        }
        citationRepository.deleteById(id);
        return citationOpt.get();
    }

}