package com.example.demo.controllers;

import com.example.demo.auth.JwtUtil;
import com.example.demo.dao.ResponseDTOs;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTOs.StatusResponse> signup(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
        if(userRepository.findByUsername(user.getUsername()).isEmpty()) {
            userRepository.save(user);
            return ResponseEntity.ok(ResponseDTOs.StatusResponse.builder().status(ResponseDTOs.Status.SUCCESS).build());
        }
        return ResponseEntity.ok(ResponseDTOs.StatusResponse.builder().status(ResponseDTOs.Status.SUCCESS).message("User already registered.").build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTOs.AuthenticationResponse> login(@RequestBody User user) throws UnsupportedEncodingException {
        if(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())).isAuthenticated()) {
            User authUser = userRepository.findByUsername(user.getUsername()).orElseThrow();
            String token = jwtUtil.generateToken(authUser, authUser.getRole());
            return ResponseEntity.ok(ResponseDTOs.AuthenticationResponse.builder().status(ResponseDTOs.Status.SUCCESS).token(token).build());
        }

        return ResponseEntity.ok(ResponseDTOs.AuthenticationResponse.builder().status(ResponseDTOs.Status.FAILURE).message("Unauthorized").build());
    }
}
