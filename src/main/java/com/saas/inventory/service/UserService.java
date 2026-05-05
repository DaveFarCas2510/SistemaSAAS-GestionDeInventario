package com.saas.inventory.service;


import com.saas.inventory.dto.UserResponseDTO;
import com.saas.inventory.model.Role;
import com.saas.inventory.model.User;
import com.saas.inventory.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user){

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.EMPLEADO);
        return userRepository.save(user);
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado!"));
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }
}
