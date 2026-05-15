package com.budgetbee.controller;

import com.budgetbee.dto.AuthResponse;
import com.budgetbee.dto.LoginRequest;
import com.budgetbee.dto.RegisterRequest;
import com.budgetbee.model.User;
import com.budgetbee.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("fullName", user.getFullName() != null ? user.getFullName() : "");
        data.put("createdAt", user.getCreatedAt());
        return ResponseEntity.ok(data);
    }
}
