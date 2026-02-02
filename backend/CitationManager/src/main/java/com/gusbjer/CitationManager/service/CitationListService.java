package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.model.CitationList;
import com.gusbjer.CitationManager.model.User;
import com.gusbjer.CitationManager.repository.CitationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link CitationList}s in the system.
 */
@Service
public class CitationListService {
    @Autowired
    private CitationListRepository citationListRepository;

    public CitationList getCitationListById(Long id) {
        return citationListRepository.findById(id).orElse(null);
    }

    public CitationList saveCitationList(CitationList citationList) {
        return citationListRepository.save(citationList);
    }

    public CitationList deleteCitationListById(Long id) {
        Optional<CitationList> citationListOpt = citationListRepository.findById(id);

        if(citationListOpt.isEmpty()) {
            return null;
        }
        citationListRepository.deleteById(id);
        return citationListOpt.get();
    }

    public List<CitationList> getCitationListsByUser(User user) {
        return citationListRepository.findByUser(user);
    }
}
