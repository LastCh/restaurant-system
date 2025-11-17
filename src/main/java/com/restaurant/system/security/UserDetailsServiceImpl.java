package com.restaurant.system.security;

import com.restaurant.system.entity.User;
import com.restaurant.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);

        User user = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> {
                    log.warn("User not found or disabled: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        log.debug("User loaded successfully: {}, role: {}", username, user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singleton(authority))
                .accountLocked(false)
                .credentialsExpired(false)
                .accountExpired(false)
                .disabled(false)
                .build();
    }
}
