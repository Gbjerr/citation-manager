package com.gusbjer.CitationManager.controller;

import com.gusbjer.CitationManager.dto.AuthTokenWrapperDto;
import com.gusbjer.CitationManager.model.Token;
import com.gusbjer.CitationManager.model.User;
import com.gusbjer.CitationManager.service.JWTService;
import com.gusbjer.CitationManager.service.TokenService;
import com.gusbjer.CitationManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exposes endpoints related to authorization for login, registration and updating access
 * and refresh tokens.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenWrapperDto> login(@RequestBody User user) {
        ResponseEntity<AuthTokenWrapperDto> response;

        AuthTokenWrapperDto authToken = userService.verifyUser(user);
        if(authToken != null) {
            User authenticatedUser = userService.getUserByUsername(user.getUsername());
            tokenService.save(authToken.getRefreshToken(), authenticatedUser);
            response = ResponseEntity.ok(authToken);
        } else {
            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokenWrapperDto> register(@RequestBody User user) {

        String attributeExistsMsg = null;
        if(userService.isUsernameAvailable(user.getUsername())) {
            attributeExistsMsg = "Username is already taken";
        } else if(userService.isEmailAvailable(user.getEmail())) {
            attributeExistsMsg = "Email is already registered";
        } else if(userService.isPasswordAvailable(user.getPassword())) {
            attributeExistsMsg = "Password is already taken";
        }

        if(attributeExistsMsg != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, attributeExistsMsg);
        }

        userService.saveUser(user);
        AuthTokenWrapperDto authToken = jwtService.generateAuthToken(user.getUsername());
        tokenService.save(authToken.getRefreshToken(), user);

        return ResponseEntity.ok(authToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenWrapperDto> refresh(@RequestHeader("Authorization") String authorizationHeader) {
        if(authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        String refreshTokenInput = authorizationHeader.substring(7);
        if(jwtService.isTokenExpired(refreshTokenInput, false)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired refresh token");
        }

        String username = jwtService.extractUserName(refreshTokenInput, false);
        Token currentToken = tokenService.findByToken(refreshTokenInput);
        if(currentToken == null || currentToken.isExpired() || currentToken.isRevoked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or revoked refresh token");
        }

        String accessToken = jwtService.generateAccessToken(username);
        String updatedRefreshToken = jwtService.generateRefreshToken(username);
        User user = userService.getUserByUsername(username);

        // Rotate the refresh token
        currentToken.setRevoked(true);
        currentToken.setExpired(true);
        tokenService.updateToken(currentToken);
        tokenService.save(updatedRefreshToken, user);

        return ResponseEntity.ok().body(
                AuthTokenWrapperDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(updatedRefreshToken)
                    .build());
    }
}
