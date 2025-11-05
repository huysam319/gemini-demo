package com.example.demo.services;

import com.example.demo.dtos.response.ApiResponse;
import jakarta.validation.constraints.Email;

public interface IUserService {
    String sendResetCode(@Email String email);
    ApiResponse<String> confirmOTP(String otp, @Email String email);
    String resetPassword(@Email String email, String newPassword, String confirmPassword);
}
