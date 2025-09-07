package com.example.parastoreb.service;

import com.example.parastoreb.dto.message.MessageRequest;
import com.example.parastoreb.dto.message.MessageResponse;

import java.util.List;

public interface MessageService {
    MessageResponse createMessage(MessageRequest request);
    List<MessageResponse> getAllMessages();
    List<MessageResponse> getUnreadMessages();
    MessageResponse getMessageById(Long id);
    MessageResponse markAsRead(Long id);
    MessageResponse respondToMessage(Long id, String response);
    long getUnreadCount();
}
