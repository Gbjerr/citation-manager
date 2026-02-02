package com.gusbjer.CitationManager.controller;

import com.gusbjer.CitationManager.dto.CitationListDto;
import com.gusbjer.CitationManager.model.Citation;
import com.gusbjer.CitationManager.model.CitationList;
import com.gusbjer.CitationManager.model.User;
import com.gusbjer.CitationManager.service.CitationListService;
import com.gusbjer.CitationManager.service.JWTService;
import com.gusbjer.CitationManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller endpoints for saving, retrieving, deleting and updating {@link CitationList}s.
 */
@RestController
@RequestMapping("/api/citationlists")
public class CitationListController {
    @Autowired
    private UserService userService;

    @Autowired
    private CitationListService citationListService;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/{id}")
    public ResponseEntity<CitationList> getCitationListById(@PathVariable("id") Long id) {
        CitationList citationList = citationListService.getCitationListById(id);
        if (citationList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(citationList);
    }

    @PostMapping()
    public ResponseEntity<CitationList> createCitationList(@RequestBody CitationListDto citationListDto) {
        User user = userService.getUserById(citationListDto.getUserId());
        CitationList citationList = CitationList.builder()
                .id(citationListDto.getId())
                .title(citationListDto.getTitle())
                .user(user)
                .build();

        CitationList createdCitationList = citationListService.saveCitationList(citationList);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCitationList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitationList> updateCitationList(@PathVariable Long id, @RequestBody CitationListDto citationListDto) {
        CitationList existingCitationList = citationListService.getCitationListById(id);
        if (existingCitationList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = userService.getUserById(citationListDto.getUserId());

        existingCitationList.setId(id);
        existingCitationList.setTitle(citationListDto.getTitle());
        existingCitationList.setUser(user);
        CitationList updatedCitationList = citationListService.saveCitationList(existingCitationList);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCitationList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CitationList> deleteCitationList(@PathVariable Long id) {
        CitationList deletedCitationList = citationListService.deleteCitationListById(id);
        if (deletedCitationList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CitationList>> getCitationListsByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<CitationList> citationLists = citationListService.getCitationListsByUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(citationLists);
    }

    @GetMapping("/me")
    public ResponseEntity<List<CitationList>> getMyCitationLists(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        User user = getUserFromAuthorization(authorizationHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<CitationList> citationLists = citationListService.getCitationListsByUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(citationLists);
    }

    @PostMapping("/me")
    public ResponseEntity<CitationList> createMyCitationList(@RequestBody CitationListDto citationListDto, @RequestHeader(value = "Authorization") String authorizationHeader) {
        User user = getUserFromAuthorization(authorizationHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CitationList citationList = CitationList.builder()
                .id(citationListDto.getId())
                .title(citationListDto.getTitle())
                .user(user)
                .build();
        CitationList createdCitationList = citationListService.saveCitationList(citationList);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCitationList);
    }

    @PutMapping("/me/{id}")
    public ResponseEntity<CitationList> updateMyCitationList(@PathVariable Long id, @RequestBody CitationListDto citationListDto, @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        User user = getUserFromAuthorization(authorizationHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CitationList existingCitationList = citationListService.getCitationListById(id);
        if (existingCitationList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        existingCitationList.setId(id);
        existingCitationList.setTitle(citationListDto.getTitle());
        existingCitationList.setUser(user);
        CitationList updatedCitationList = citationListService.saveCitationList(existingCitationList);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCitationList);
    }

    /**
     * Retrieves {@link User} based on the JWT auth header.
     */
    private User getUserFromAuthorization(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        String jwt = authorizationHeader.substring(7);
        String username = jwtService.extractUserName(jwt, true);
        return userService.getUserByUsername(username);
    }
}
