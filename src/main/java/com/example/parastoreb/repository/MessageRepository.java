package com.example.parastoreb.repository;

import com.example.parastoreb.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Récupérer tous les messages triés par date de création (plus récents en premier)
    List<Message> findAllByOrderByCreatedAtDesc();
    
    // Récupérer les messages par statut
    List<Message> findByStatusOrderByCreatedAtDesc(Message.MessageStatus status);
    
    // Compter les messages non lus
    long countByStatus(Message.MessageStatus status);
    
    // Récupérer les messages non lus
    @Query("SELECT m FROM Message m WHERE m.status = 'NEW' ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessages();
}
