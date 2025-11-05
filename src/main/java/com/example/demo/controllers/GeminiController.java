package com.example.demo.controllers;

import com.example.demo.services.IGeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {
    @Autowired
    private IGeminiService geminiService;

    @GetMapping("/ask")
    public String askGeminiAPI(@RequestBody String prompt) {
        return this.geminiService.askGemini(prompt);
    }

    @PostMapping("/analyze")
    public String analyzeCv(@RequestParam("path") String path) throws Exception {
        return this.geminiService.analyzeCv(Files.readAllBytes(Paths.get(path)));
    }

    @PostMapping("/plan")
    public String generateActionPlan(@RequestBody Object cvAnalysis,
                                     @RequestParam("target") String target,
                                     @RequestParam("complete_time") LocalDateTime completeTime,
                                     @RequestParam("current_time") LocalDateTime currentTime) {
        return this.geminiService.generateActionPlan(cvAnalysis, target, completeTime, currentTime);
    }
}
