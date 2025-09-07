package com.example.parastoreb.service;

import com.example.parastoreb.entity.User;
import com.example.parastoreb.entity.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    List<User> getAllUsers();
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByEmail(String email);
    
    User updateUserRole(Long userId, Role newRole);
    
    boolean deleteUser(Long userId);
    
    List<User> getUsersByRole(Role role);
    
    boolean isAdmin(String email);
}
