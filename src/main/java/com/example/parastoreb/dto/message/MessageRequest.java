package com.example.parastoreb.dto.message;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class MessageRequest {
    @NotBlank(message = "Le nom complet est requis")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String fullName;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le message est requis")
    @Size(min = 10, max = 1000, message = "Le message doit contenir entre 10 et 1000 caractères")
    private String message;
}
