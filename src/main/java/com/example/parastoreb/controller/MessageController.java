package com.example.parastoreb.controller;

import com.example.parastoreb.dto.message.MessageRequest;
import com.example.parastoreb.dto.message.MessageResponse;
import com.example.parastoreb.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MessageController {

    private final MessageService messageService;

    // Endpoint public pour envoyer un message (page Contact)
    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@Valid @RequestBody MessageRequest request) {
        MessageResponse response = messageService.createMessage(request);
        return ResponseEntity.ok(response);
    }

    // Endpoints pour le gestionnaire (authentification requise)
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages() {
        List<MessageResponse> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<MessageResponse>> getUnreadMessages() {
        List<MessageResponse> messages = messageService.getUnreadMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUnreadCount() {
        long count = messageService.getUnreadCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable Long id) {
        MessageResponse message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<MessageResponse> markAsRead(@PathVariable Long id) {
        MessageResponse message = messageService.markAsRead(id);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}/respond")
    public ResponseEntity<MessageResponse> respondToMessage(
            @PathVariable Long id, 
            @RequestBody String response) {
        MessageResponse message = messageService.respondToMessage(id, response);
        return ResponseEntity.ok(message);
    }
}
