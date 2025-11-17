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

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and authentication endpoints")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // Register new user (CLIENT role by default)
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

    // Authenticate user and return access + refresh tokens
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

        // Generate both access and refresh tokens
        String accessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getRole().toString());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        log.info("User logged in successfully: {}", request.getUsername());

        return ResponseEntity.ok(JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().toString())
                .expiresIn(jwtProvider.getAccessTokenExpirationMs())
                .build());
    }

    // Refresh access token using refresh token
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request received");

        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            log.warn("Invalid or expired refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid or expired refresh token"));
        }

        // Extract username from refresh token
        String username = jwtProvider.getUsernameFromRefreshToken(refreshToken);
        if (username == null) {
            log.error("Failed to extract username from refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid refresh token"));
        }

        // Verify user still exists and is enabled
        User user = userRepository.findByUsernameAndEnabledTrue(username)
                .orElse(null);

        if (user == null) {
            log.warn("User not found or disabled: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("User not found or disabled"));
        }

        // Generate new access token
        String newAccessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getRole().toString());

        log.info("Access token refreshed successfully for user: {}", username);

        return ResponseEntity.ok(RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getAccessTokenExpirationMs())
                .build());
    }

    // Validate access token
    @PostMapping("/validate")
    @Operation(summary = "Validate JWT access token")
    public ResponseEntity<?> validateToken(@Valid @RequestBody TokenValidationRequest request) {
        boolean isValid = jwtProvider.validateAccessToken(request.getToken());

        if (isValid) {
            String username = jwtProvider.getUsernameFromAccessToken(request.getToken());
            String role = jwtProvider.getRoleFromAccessToken(request.getToken());
            return ResponseEntity.ok(new TokenValidationResponse(true, username, role));
        }

        return ResponseEntity.ok(new TokenValidationResponse(false, null, null));
    }
}
