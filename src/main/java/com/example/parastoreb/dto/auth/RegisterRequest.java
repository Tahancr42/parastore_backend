package com.example.parastoreb.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role; // ROLE_CLIENT, ROLE_ADMIN, ROLE_ACHAT
}
