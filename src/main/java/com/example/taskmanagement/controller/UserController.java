package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.UserDto;
import com.example.taskmanagement.security.UserDetailsImpl;
import com.example.taskmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getCurrentUser();
    }
}
