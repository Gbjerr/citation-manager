package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.CitationList;
import com.gusbjer.CitationManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitationListRepository extends JpaRepository<CitationList, Long> {
    List<CitationList> findByUser(User user);
}
