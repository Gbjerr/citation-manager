package com.gusbjer.CitationManager.repository;

import com.gusbjer.CitationManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByPassword(String password);
    User findByEmail(String email);
}
