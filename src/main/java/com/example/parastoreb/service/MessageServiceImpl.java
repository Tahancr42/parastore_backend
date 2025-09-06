package com.example.parastoreb.service;

import com.example.parastoreb.dto.message.MessageRequest;
import com.example.parastoreb.dto.message.MessageResponse;
import com.example.parastoreb.entity.Message;
import com.example.parastoreb.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public MessageResponse createMessage(MessageRequest request) {
        Message message = Message.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .message(request.getMessage())
                .status(Message.MessageStatus.NEW)
                .build();

        Message savedMessage = messageRepository.save(message);
        return mapToResponse(savedMessage);
    }

    @Override
    public List<MessageResponse> getAllMessages() {
        return messageRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<MessageResponse> getUnreadMessages() {
        return messageRepository.findUnreadMessages()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MessageResponse getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        return mapToResponse(message);
    }

    @Override
    @Transactional
    public MessageResponse markAsRead(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        if (message.getStatus() == Message.MessageStatus.NEW) {
            message.setStatus(Message.MessageStatus.READ);
            message.setReadAt(LocalDateTime.now());
            message = messageRepository.save(message);
        }

        return mapToResponse(message);
    }

    @Override
    @Transactional
    public MessageResponse respondToMessage(Long id, String response) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        message.setResponse(response);
        message.setStatus(Message.MessageStatus.RESPONDED);
        message.setRespondedAt(LocalDateTime.now());
        
        if (message.getReadAt() == null) {
            message.setReadAt(LocalDateTime.now());
        }

        Message savedMessage = messageRepository.save(message);
        return mapToResponse(savedMessage);
    }

    @Override
    public long getUnreadCount() {
        return messageRepository.countByStatus(Message.MessageStatus.NEW);
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .fullName(message.getFullName())
                .email(message.getEmail())
                .message(message.getMessage())
                .status(message.getStatus().name())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .response(message.getResponse())
                .respondedAt(message.getRespondedAt())
                .build();
    }
}
