package com.example.demo.controllers;

import com.example.demo.dtos.response.ApiResponse;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/confirm-otp")
    public ResponseEntity<ApiResponse<String>> confirmOtp(@RequestParam String otp, @RequestParam String email) {
        return ResponseEntity.ok(userService.confirmOTP(otp, email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        return ResponseEntity.ok(userService.resetPassword(email, newPassword, confirmPassword));
    }
}
