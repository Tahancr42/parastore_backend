package com.example.parastoreb.controller;

import com.example.parastoreb.entity.User;
import com.example.parastoreb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TestController {

    private final UserRepository userRepository;

    // === TEST 1: Vérifier que l'admin existe et ses propriétés ===
    @GetMapping("/admin-exists")
    public ResponseEntity<Map<String, Object>> testAdminExists() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var adminOpt = userRepository.findByEmail("admin@gmail.com");
            
            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                result.put("exists", true);
                result.put("email", admin.getEmail());
                result.put("role", admin.getRole());
                result.put("enabled", admin.isEnabled());
                result.put("passwordHash", admin.getPassword());
                
                // Test BCrypt
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                boolean matches = encoder.matches("admin", admin.getPassword());
                result.put("bcryptMatches", matches);
                
                log.info("✅ Admin trouvé: email={}, role={}, enabled={}, bcryptMatches={}", 
                    admin.getEmail(), admin.getRole(), admin.isEnabled(), matches);
            } else {
                result.put("exists", false);
                log.warn("❌ Admin non trouvé dans la base");
            }
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            log.error("❌ Erreur lors de la vérification admin: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    // === TEST 2: Test direct de l'endpoint /login (sans contrainte de rôle) ===
    @PostMapping("/test-login")
    public ResponseEntity<Map<String, Object>> testLogin(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        String email = request.get("email");
        String password = request.get("password");
        
        log.info("🧪 Test login direct avec email: '{}'", email);
        
        try {
            var userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                result.put("userFound", true);
                result.put("email", user.getEmail());
                result.put("role", user.getRole());
                result.put("enabled", user.isEnabled());
                
                // Test BCrypt
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                boolean matches = encoder.matches(password, user.getPassword());
                result.put("bcryptMatches", matches);
                
                if (matches) {
                    result.put("authSuccess", true);
                    result.put("message", "Authentification réussie !");
                    log.info("✅ Test login réussi pour: {}", email);
                } else {
                    result.put("authSuccess", false);
                    result.put("message", "Mot de passe incorrect");
                    log.warn("❌ Mot de passe incorrect pour: {}", email);
                }
            } else {
                result.put("userFound", false);
                result.put("message", "Utilisateur non trouvé");
                log.warn("❌ Utilisateur non trouvé: {}", email);
            }
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            log.error("❌ Erreur lors du test login: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    // === TEST 3: Vérifier tous les utilisateurs ===
    @GetMapping("/all-users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var users = userRepository.findAll();
            result.put("count", users.size());
            result.put("users", users.stream().map(user -> Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "enabled", user.isEnabled()
            )).toList());
            
            log.info("📊 {} utilisateurs trouvés", users.size());
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            log.error("❌ Erreur lors de la récupération des utilisateurs: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    // === TEST 4: Health check ===
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> result = Map.of(
            "status", "OK",
            "message", "Backend opérationnel"
        );
        log.info("🏥 Health check OK");
        return ResponseEntity.ok(result);
    }

    // === TEST 5: Test direct de connexion admin ===
    @PostMapping("/test-admin-login")
    public ResponseEntity<Map<String, Object>> testAdminLogin() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            var adminOpt = userRepository.findByEmail("admin@gmail.com");
            
            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                result.put("adminExists", true);
                result.put("email", admin.getEmail());
                result.put("role", admin.getRole());
                result.put("enabled", admin.isEnabled());
                
                // Test BCrypt avec le mot de passe "admin"
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                boolean matches = encoder.matches("admin", admin.getPassword());
                result.put("passwordMatches", matches);
                
                if (matches) {
                    result.put("status", "SUCCESS");
                    result.put("message", "Compte admin correct !");
                    log.info("✅ Compte admin vérifié et fonctionnel");
                } else {
                    result.put("status", "ERROR");
                    result.put("message", "Mot de passe admin incorrect !");
                    log.error("❌ Mot de passe admin incorrect");
                }
            } else {
                result.put("adminExists", false);
                result.put("status", "ERROR");
                result.put("message", "Compte admin non trouvé !");
                log.error("❌ Compte admin non trouvé");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            log.error("❌ Erreur lors de la vérification admin: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    // === CORRECTION AUTOMATIQUE ADMIN ===
    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, Object>> createAdmin() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Supprimer l'ancien admin s'il existe
            userRepository.deleteByEmail("admin@gmail.com");
            
            // Créer le nouveau admin avec le bon hash
            User admin = User.builder()
                    .email("admin@gmail.com")
                    .password("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa")
                    .role(com.example.parastoreb.entity.Role.ADMIN)
                    .enabled(true)
                    .name("Administrateur")
                    .build();
            
            User savedAdmin = userRepository.save(admin);
            
            result.put("success", true);
            result.put("message", "Compte admin créé avec succès !");
            result.put("adminId", savedAdmin.getId());
            result.put("email", savedAdmin.getEmail());
            result.put("role", savedAdmin.getRole());
            
            log.info("✅ Compte admin créé automatiquement avec ID: {}", savedAdmin.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("❌ Erreur lors de la création de l'admin: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
}
