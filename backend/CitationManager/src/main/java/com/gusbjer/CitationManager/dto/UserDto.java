package com.gusbjer.CitationManager.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for registered users.
 */
@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
}
