package com.example.demo.services.impl;

import org.apache.tika.Tika;
import com.example.demo.services.IGeminiService;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GeminiService implements IGeminiService {
    @Autowired
    private Client client;

    private final Tika tika = new Tika();

    @Override
    public String askGemini(String prompt) {
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null);

        return response.text();
    }

    public String analyzeCv(byte[] fileBytes) throws Exception {
        // Trích xuất text từ PDF/DOCX
        String content = tika.parseToString(new java.io.ByteArrayInputStream(fileBytes));

        // Prompt yêu cầu phân tích và trả về JSON chuẩn
        String prompt = """
        You are a CV parser. Analyze the following CV text and return a structured JSON in this format only:
        {
            "basic information": {"Name": "", "Phone": "", "Email": "", "Address": ""},
            "education": ["School": "", "Certificate": "", "Year": ""],
            "experience": ["Company": "", "Period": "", "Role": []],
            "skill": {"technical skills": [], "soft skills": []},
            "summary": "",
            "career path": ""
        }
        The summary attribute must use 'you' to indicate the user. The career path attribute suggest briefly the career direction for user.
        Note that the experience attribute here indicates the working experience, not the project experience.
        And the Role inner attribute indicates the position (e.g. Intern, Junior, Senior...) and the field
        (e.g. Backend, Frontend, Full-stack, AI...).
        If any field does not exists in the CV, make it null.
        CV text:
        """ + content;

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null);

        return response.text(); // JSON string
    }

    @Override
    public String generateActionPlan(Object cvAnalysis, String target, LocalDateTime completeTime, LocalDateTime currentTime) {
        String cvJson = cvAnalysis.toString();

        String prompt = """
        You are an expert career advisor and skill development planner.
        Your task:
        - Analyze the candidate’s skills from the provided CV JSON.
        - Create a personalized, step-by-step detail action plan from the current time
        that helps the candidate reach the target within the given time frame.
        - The action plan must include phases to achieve the skills (e.g. Foundations, Development, Application, Advanced...).
        - The deadline attribute must not be before the current date.
        - The output must be structured in clean JSON with the following format:
        [
            {
                "big_title": "" (string length=100)
                "route": [
                    "small_title": "" (string length=100)
                    "percentage": 0 (int always = 0)
                    "description": "" (string length=255)
                    "deadline": "2025-11-05T14:04:40+07:00" (date ISO 8601)
                ]
            }
        ]
        CV json:
        """ + cvJson +
                ", target: " + target +
                ", time to complete: " + completeTime.toString() +
                ", current time: " + currentTime.toString();

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null);

        return response.text();
    }
}
