package com.gusbjer.CitationManager.controller;

import com.gusbjer.CitationManager.dto.AuthTokenWrapperDto;
import com.gusbjer.CitationManager.dto.UserDto;
import com.gusbjer.CitationManager.model.User;
import com.gusbjer.CitationManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller handling retrieval, deleting and creating {@link User}s in the system.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenWrapperDto> login(@RequestBody User user) {
        AuthTokenWrapperDto authToken = userService.verifyUser(user);
        return authToken == null ?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null) :
                ResponseEntity.ok(authToken);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping
    public User updateUser(@RequestBody UserDto userDetails) {
        return userService.updateUser(userDetails);
    }

    @DeleteMapping
    public String deleteAllUsers() {
        userService.deleteAllUsers();
        return "All users have been deleted successfully.";
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
