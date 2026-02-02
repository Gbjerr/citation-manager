package com.gusbjer.CitationManager.service;

import static com.gusbjer.CitationManager.security.SecurityConfig.getEncoder;

import com.gusbjer.CitationManager.dto.UserDto;
import com.gusbjer.CitationManager.model.User;
import com.gusbjer.CitationManager.repository.UserRepository;
import com.gusbjer.CitationManager.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link User}s in the system.
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        user.setPassword(getEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(UserDto userDetails) {
        Optional<User> user = userRepository.findById(userDetails.getId());
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPassword(getEncoder().encode(userDetails.getPassword()));
            return userRepository.save(existingUser);
        }
        return null;
    }

    public boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public boolean isPasswordAvailable(String password) {
        String hashedPassword = SecurityConfig.getEncoder().encode(password);
        return userRepository.findByPassword(hashedPassword) != null;
    }

    public boolean isEmailAvailable(String mail) {
        return userRepository.findByEmail(mail) != null;
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public User deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);

        if(userOpt.isEmpty()) {
            return null;
        }
        userRepository.deleteById(id);
        return userOpt.get();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
