package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {
}
