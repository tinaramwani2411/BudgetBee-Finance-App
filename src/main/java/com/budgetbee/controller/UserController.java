package com.budgetbee.controller;

import com.budgetbee.model.User;
import com.budgetbee.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            Principal principal,
            @RequestBody Map<String, String> body) {

        User user = userService.updateProfile(
                principal.getName(),
                body.get("fullName"),
                body.get("email")
        );

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName() != null ? user.getFullName() : "",
                "message", "Profile updated successfully"
        ));
    }
}
