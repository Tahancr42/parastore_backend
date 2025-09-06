package com.example.parastoreb.dto.message;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long id;
    private String fullName;
    private String email;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String response;
    private LocalDateTime respondedAt;
}
