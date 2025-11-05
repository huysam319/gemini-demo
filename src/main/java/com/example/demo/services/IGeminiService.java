package com.example.demo.services;

import java.time.LocalDateTime;

public interface IGeminiService {
    String askGemini(String prompt);
    String analyzeCv(byte[] fileBytes) throws Exception;
    String generateActionPlan(Object cvAnalysis, String target, LocalDateTime completeTime, LocalDateTime currentTime);
}
