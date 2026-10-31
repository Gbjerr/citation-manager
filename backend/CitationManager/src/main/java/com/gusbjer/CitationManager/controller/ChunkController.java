package com.gusbjer.CitationManager.controller;

import com.gusbjer.CitationManager.dto.ChunkSimilarityDto;
import com.gusbjer.CitationManager.dto.InputTextSimilarityDto;
import com.gusbjer.CitationManager.service.ChunkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chunks")
public class ChunkController {

    @Autowired
    private ChunkService chunkService;

    @PostMapping("/similarity")
    public List<ChunkSimilarityDto> getSimilarChunks(@RequestBody InputTextSimilarityDto inputTextSimilarityDto) {
        List<ChunkSimilarityDto> similarChunks = chunkService.getSimilarCitations(inputTextSimilarityDto.getInputText(), inputTextSimilarityDto.getCitationListId());
        return similarChunks;
    }
}
