package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {
}
