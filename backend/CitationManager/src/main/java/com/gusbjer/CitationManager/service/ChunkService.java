package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.model.Chunk;
import com.gusbjer.CitationManager.repository.ChunkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChunkService {

    @Autowired
    private ChunkRepository chunkRepository;

    public List<Chunk> saveAll(List<Chunk> chunks) {
        return chunkRepository.saveAll(chunks);
    }
}
