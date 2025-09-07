package com.example.parastoreb.mapper;

import com.example.parastoreb.entity.User;
import com.example.parastoreb.entity.Role;
import com.example.parastoreb.dto.user.UserDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole()) // Plus besoin de Role.valueOf() car c'est déjà un enum
                .enabled(user.isEnabled())
                .build();
    }

    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .role(userDto.getRole()) // Plus besoin de .name() car c'est déjà un enum
                .enabled(userDto.isEnabled())
                .build();
    }

    public List<UserDto> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
