package com.example.parastoreb.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;   // <<< IMPORTANT
// ^ sans cet import, "List" sera en rouge

@Service
public class JwtService {

    // ⚠️ en prod, mets-les dans application.properties et injecte via @Value
    private static final String SECRET_KEY = "TAHA_SUPER_SECRET";
    private static final long EXPIRATION_TIME_MS = 86400000L; // 24h

    /** Token simple (compat avec ton ancien code) */
    public String generateToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /** Token avec UN rôle (ex: "ROLE_ADMIN") */
    public String generateToken(String email, String roleAuthority) {
        // Utilise array claim pour éviter tout warning d'IDE
        return JWT.create()
                .withSubject(email)
                .withArrayClaim("roles", new String[]{ roleAuthority })
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /** Token avec PLUSIEURS rôles (ex: ["ROLE_ADMIN","ROLE_X"]) */
    public String generateToken(String email, List<String> roleAuthorities) {
        return JWT.create()
                .withSubject(email)
                // Certains IDE buguent sur withClaim(String, List<?>). Utilisons array claim :
                .withArrayClaim("roles", roleAuthorities.toArray(new String[0]))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String extractUsername(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            String extractedEmail = extractUsername(token);
            return extractedEmail.equals(email);
        } catch (Exception e) {
            return false;
        }
    }
}
