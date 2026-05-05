package com.saas.inventory.dto;

import lombok.Getter;

@Getter
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String role;

    public UserResponseDTO(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

}
