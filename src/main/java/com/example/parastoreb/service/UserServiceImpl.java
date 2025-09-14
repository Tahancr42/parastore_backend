package com.example.parastoreb.service;

import com.example.parastoreb.entity.User;
import com.example.parastoreb.entity.Role;
import com.example.parastoreb.repository.UserRepository;
import com.example.parastoreb.controller.UserController.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Empêcher la modification du rôle admin
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Impossible de modifier le rôle d'un administrateur");
        }
        
        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Override
    public boolean deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Empêcher la suppression d'un admin
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Impossible de supprimer un administrateur");
        }
        
        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User updateUserProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new RuntimeException("Cette adresse email est déjà utilisée");
            }
        }
        
        // Mettre à jour les champs fournis
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        
        return userRepository.save(user);
    }

    @Override
    public boolean isAdmin(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && user.get().getRole() == Role.ADMIN;
    }
}
