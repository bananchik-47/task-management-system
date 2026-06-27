package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.AuthResponse;
import com.example.taskmanagement.dto.LoginRequest;
import com.example.taskmanagement.dto.RegisterRequest;
import com.example.taskmanagement.dto.UserDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.exception.ConflictException;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.mapper.UserMapper;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim();

        log.info("Registering user: username={}, email={}", username, email);

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already exists: " + email);
        }

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        log.debug("Saving user before persist: username={}", username);
        User savedUser = userRepository.saveAndFlush(user);
        log.info("User saved successfully: id={}, username={}", savedUser.getId(), savedUser.getUsername());

        String token = jwtUtils.generateToken(savedUser.getUsername());
        log.debug("JWT generated for user: {}", savedUser.getUsername());

        return AuthResponse.builder().token(token).build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtils.generateToken(request.getUsername());
        return AuthResponse.builder().token(token).build();
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        User user = getAuthenticatedUser();
        return userMapper.toDto(user);
    }

    User getAuthenticatedUser() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
