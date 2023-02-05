package com.example.cloudstorageservice.security;

import com.example.cloudstorageservice.model.db.UserEntity;
import com.example.cloudstorageservice.model.exception.UserNotFoundException;
import com.example.cloudstorageservice.repository.SecurityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final SecurityRepository securityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity currentUser = securityRepository.findById(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(currentUser.getUsername(), currentUser.getPassword(), roles);
    }

    public String getUsernameByContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
