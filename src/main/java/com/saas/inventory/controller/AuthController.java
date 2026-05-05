package com.saas.inventory.controller;


import com.saas.inventory.dto.AuthRequest;
import com.saas.inventory.dto.AuthResponse;
import com.saas.inventory.model.User;
import com.saas.inventory.security.JwtUtil;
import com.saas.inventory.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtill) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtill;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request){
        User user = userService.findByUsername(request.getUsername());

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Credenciales Incorrectas!");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name());

        return new AuthResponse(token);
    }
}
