package com.example.parastoreb.controller;

import com.example.parastoreb.entity.User;
import com.example.parastoreb.entity.Role;
import com.example.parastoreb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Récupération de l'utilisateur avec l'ID: {}", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role newRole) {
        log.info("Mise à jour du rôle de l'utilisateur {} vers {}", id, newRole);
        try {
            User updatedUser = userService.updateUserRole(id, newRole);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du rôle: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Suppression de l'utilisateur avec l'ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        log.info("Récupération des utilisateurs avec le rôle: {}", role);
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    // Endpoint de test pour vérifier l'admin (sans authentification)
    @GetMapping("/test-admin")
    public ResponseEntity<String> testAdmin() {
        log.info("Test de l'endpoint admin");
        return ResponseEntity.ok("Endpoint admin accessible - l'authentification fonctionne !");
    }
}
