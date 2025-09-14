package com.example.parastoreb.controller;

import com.example.parastoreb.entity.User;
import com.example.parastoreb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Récupérer les informations de l'utilisateur connecté
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'CLIENT')")
    public ResponseEntity<User> getProfile(@RequestParam Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Modifier les informations de l'utilisateur connecté
    @PutMapping("/profile/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'CLIENT')")
    public ResponseEntity<User> updateProfile(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequest request) {
        try {
            User updatedUser = userService.updateUserProfile(userId, request);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Classe interne pour la requête de mise à jour
    public static class UserUpdateRequest {
        private String name;
        private String phone;
        private String email;

        // Constructeurs
        public UserUpdateRequest() {}

        public UserUpdateRequest(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        // Getters et Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
