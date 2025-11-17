package com.restaurant.system.controller;

import com.restaurant.system.dto.auth.*;
import com.restaurant.system.entity.User;
import com.restaurant.system.repository.UserRepository;
import com.restaurant.system.security.JwtProvider;
import com.restaurant.system.security.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and authentication endpoints")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    @Operation(summary = "Register a new client")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
        log.info("New signup request for username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Username already exists"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.CLIENT)
                .enabled(true)
                .build();


        userRepository.save(user);

        log.info("User registered successfully: {}", request.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully. You can now login."));
    }

    @PostMapping("/signin")
    @Operation(summary = "User login")
    public ResponseEntity<?> signin(@Valid @RequestBody SignInRequest request) {
        log.info("Login request for username: {}", request.getUsername());

        User user = userRepository.findByUsernameAndEnabledTrue(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid login attempt for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password"));
        }

        String token = jwtProvider.generateToken(user.getUsername(), user.getRole().toString());

        log.info("User logged in successfully: {}", request.getUsername());

        return ResponseEntity.ok(new JwtResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getRole().toString(),
                jwtProvider.getExpirationMs()
        ));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token")
    public ResponseEntity<?> validateToken(@Valid @RequestBody TokenValidationRequest request) {
        boolean isValid = jwtProvider.validateToken(request.getToken());

        if (isValid) {
            String username = jwtProvider.getUsernameFromToken(request.getToken());
            String role = jwtProvider.getRoleFromToken(request.getToken());
            return ResponseEntity.ok(new TokenValidationResponse(true, username, role));
        }

        return ResponseEntity.ok(new TokenValidationResponse(false, null, null));
    }
}
