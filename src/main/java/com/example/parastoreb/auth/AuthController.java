package com.example.parastoreb.auth;

import com.example.parastoreb.dto.auth.LoginResponse;
import com.example.parastoreb.dto.auth.LoginRequest;
import com.example.parastoreb.dto.auth.RegisterRequest;
import com.example.parastoreb.entity.Role;
import com.example.parastoreb.entity.User;
import com.example.parastoreb.repository.UserRepository;
import com.example.parastoreb.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    // === REGISTER ===
    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest req) {
        // Trim l'email et le mot de passe pour éliminer les espaces parasites
        String email = req.getEmail().trim();
        String password = req.getPassword().trim();

        log.info("Tentative d'inscription pour l'email: '{}' (après trim)", email);

        // Bloquer l'admin - le compte admin doit être créé manuellement
        if ("admin@gmail.com".equalsIgnoreCase(email)) {
            log.warn("Tentative de création d'admin via /register bloquée");
            throw new IllegalArgumentException("Création de l'admin interdite via /register. Le compte admin doit être créé manuellement.");
        }
        // Email déjà utilisé
        if (userRepository.existsByEmail(email)) {
            log.warn("Email déjà utilisé: {}", email);
            throw new IllegalArgumentException("Email déjà utilisé.");
        }

        // Créer un nouvel utilisateur CLIENT
        User user = User.builder()
                .name(req.getName())
                .email(email)
                .phone(req.getPhone())
                .password(passwordEncoder.encode(password))
                .role(Role.CLIENT)  // Utilisation de l'enum
                .enabled(true)
                .build();

        userRepository.save(user);
        log.info("Utilisateur créé avec succès: {} avec le rôle: {}", user.getEmail(), user.getRole());

        String token = jwtService.generateToken(
                user.getEmail(),
                "ROLE_" + user.getRole().name() // .name() pour convertir enum en String + préfixe ROLE_
        );

        // Redirection par défaut pour un client
        String redirectUrl = "/";

        return new LoginResponse(
                token,
                user.getRole().name(), // .name() pour convertir enum en String
                user.getId(),
                redirectUrl
        );
    }

    // === LOGIN UNIQUE POUR TOUS LES RÔLES ===
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        // Trim l'email et le mot de passe pour éliminer les espaces parasites
        String email = req.getEmail().trim();
        String password = req.getPassword().trim();

        log.info("Tentative de connexion pour l'email: '{}' (après trim)", email);

        try {
            // Authentifier avec Spring Security (avec les valeurs trimmées)
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            log.info("Authentification réussie pour: {}", email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé."));

            log.info("Utilisateur trouvé: {} avec le rôle: {}", user.getEmail(), user.getRole());

            String token = jwtService.generateToken(
                    user.getEmail(),
                    "ROLE_" + user.getRole().name() // .name() pour convertir enum en String + préfixe ROLE_
            );

            // Définir la redirection en fonction du rôle
            String redirectUrl = "/";
            if (user.getRole() == Role.ADMIN) {
                redirectUrl = "/admin";
            } else if (user.getRole() == Role.GESTIONNAIRE) {
                redirectUrl = "/gestionnaire";
            } else if (user.getRole() == Role.CLIENT) {
                redirectUrl = "/";
            }

            log.info("Connexion réussie pour: {} avec le rôle: {} et redirection vers: {}",
                    user.getEmail(), user.getRole(), redirectUrl);

            return new LoginResponse(
                    token,
                    user.getRole().name(), // .name() pour convertir enum en String
                    user.getId(),
                    redirectUrl
            );
        } catch (Exception e) {
            log.error("Erreur lors de la connexion pour '{}': {}", email, e.getMessage());
            throw e;
        }
    }
}