package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.model.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;

@Service
public class LogoutService implements LogoutHandler {

    @Autowired
    private TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshTokenValue = null;
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JSONObject jsonObject = new JSONObject(body);
            refreshTokenValue = jsonObject.getString("refreshToken");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Token refreshToken = tokenService.findByToken(refreshTokenValue);
        if(refreshToken != null) {
            refreshToken.setExpired(true);
            refreshToken.setRevoked(true);
            tokenService.updateToken(refreshToken);
        }

        SecurityContextHolder.clearContext();
    }
}
