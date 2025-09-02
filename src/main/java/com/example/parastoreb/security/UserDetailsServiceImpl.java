package com.example.parastoreb.security;

import com.example.parastoreb.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import com.example.parastoreb.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Tentative de chargement de l'utilisateur avec l'email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé avec l'email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.info("Utilisateur trouvé: {} avec le rôle: {}", user.getEmail(), user.getRole());
        
        // Créer l'autorité avec le préfixe ROLE_ et le nom de l'enum
        String authority = "ROLE_" + user.getRole().name();
        log.info("Autorité créée: {}", authority);
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority(authority))
        );
    }
}
