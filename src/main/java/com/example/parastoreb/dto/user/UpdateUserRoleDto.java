package com.example.parastoreb.dto.user;

import com.example.parastoreb.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleDto {
    private Long userId;
    private Role newRole;
}
