package com.saas.inventory.controller;


import com.saas.inventory.dto.UserResponseDTO;
import com.saas.inventory.model.User;
import com.saas.inventory.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDTO create(@RequestBody User user) {
        User savedUser = userService.save(user);
        return userService.toDTO(savedUser);
    }

    @GetMapping
    public List<User> getAll(){
        return userService.getAll();
    }

    @PostMapping("/test")
    public String test() {
        return "FUNCIONA";
    }

}
