package com.example.demo.services.impl;

import com.example.demo.dtos.response.ApiResponse;
import com.example.demo.entities.PasswordResetToken;
import com.example.demo.entities.User;
import com.example.demo.repositories.PasswordResetTokenRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.IUserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String sendResetCode(@Email String email) {
        Optional<User> accountOpt = userRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            return "Email không tồn tại!";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        Optional<PasswordResetToken> existingTokenOpt = tokenRepository.findByEmail(email);

        if (existingTokenOpt.isPresent()) {
            PasswordResetToken existingToken = existingTokenOpt.get();
            existingToken.setToken(otp);
            existingToken.setExpiryDate(expiryTime);
            tokenRepository.save(existingToken);
        } else {
            PasswordResetToken token = new PasswordResetToken(email, otp, expiryTime);
            tokenRepository.save(token);
        }

        sendEmail(email, "Quên mật khẩu - Mã OTP", "Mã OTP của bạn là: " + otp);

        return "Mã OTP đã được gửi tới email.";
    }

    public void sendEmail(@Email String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public ApiResponse<String> confirmOTP(String otp, @Email String email){
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndToken(email, otp);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(400, "OTP không hợp lệ hoặc đã hết hạn", null);
        }
        return new ApiResponse<>(200, "Mã OTP hợp lệ", null);
    }

    @Transactional
    public String resetPassword(@Email String email, String newPassword, String confirmPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Email không tồn tại!";
        }

        User user = userOpt.get();
        if(newPassword.equals(confirmPassword)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            tokenRepository.deleteByEmail(email);
            return "Successful";
        }
        else {return "Password isn't match";}
    }
}
