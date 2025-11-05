package com.example.demo.controllers;

import com.example.demo.dtos.request.UserCreationRequest;
import com.example.demo.dtos.response.ApiResponse;
import com.example.demo.dtos.response.UserResponse;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.IUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> sendResetCode(@RequestParam String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        String message = userService.sendResetCode(email);
        return ResponseEntity.ok(new ApiResponse<>(200, message, null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(new ApiResponse<>(200, "Successfully", users));
    }

    @PostMapping("/confirm-otp")
    public ResponseEntity<ApiResponse<String>> confirmOtp(@RequestParam String otp, @RequestParam String email) {
        return ResponseEntity.ok(userService.confirmOTP(otp, email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        return ResponseEntity.ok(userService.resetPassword(email, newPassword, confirmPassword));
    }
}
