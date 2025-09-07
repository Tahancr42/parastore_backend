package com.example.parastoreb.auth;

import com.example.parastoreb.dto.auth.LoginRequest;
import com.example.parastoreb.dto.auth.LoginResponse;
import com.example.parastoreb.dto.auth.RegisterRequest;
import com.example.parastoreb.entity.Role;
import com.example.parastoreb.entity.User;
import com.example.parastoreb.repository.UserRepository;
import com.example.parastoreb.security.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    @Override
    public LoginResponse register(RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT) // Utilisation de l'enum
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), "ROLE_" + user.getRole().name()); // .name() pour convertir enum en String + préfixe ROLE_

        // Après inscription, rediriger vers la page de connexion
        String redirectUrl = "/login";

        return new LoginResponse(token, user.getRole().name(), user.getId(), redirectUrl);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = jwtService.generateToken(user.getEmail(), "ROLE_" + user.getRole().name()); // .name() pour convertir enum en String + préfixe ROLE_

        // Définir la redirection en fonction du rôle
        String redirectUrl = "/";
        if (user.getRole() == Role.ADMIN) {
            redirectUrl = "/admin";
        } else if (user.getRole() == Role.GESTIONNAIRE) {
            redirectUrl = "/gestionnaire";
        } else if (user.getRole() == Role.CLIENT) {
            redirectUrl = "/";
        }

        return new LoginResponse(token, user.getRole().name(), user.getId(), redirectUrl);
    }
}
