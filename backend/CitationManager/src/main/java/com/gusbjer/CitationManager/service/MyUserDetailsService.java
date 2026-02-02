package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.model.MyUserDetails;
import com.gusbjer.CitationManager.model.User;
import com.gusbjer.CitationManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            String errorMsg = "Username \"" + username + " was not found in the database.\"";
            System.out.println(errorMsg);
            throw new UsernameNotFoundException(errorMsg);
        }
        return new MyUserDetails(user);
    }
}
