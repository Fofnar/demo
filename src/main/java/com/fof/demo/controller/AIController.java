package com.fof.demo.controller;

import com.fof.demo.dto.AIResponse;
import com.fof.demo.dto.ApiResponse;
import com.fof.demo.service.AIAnalysisService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

// Déclare un contrôleur REST
@RestController

// URL de base
@RequestMapping("/api/ai")

@RequiredArgsConstructor
public class AIController {

    // Injection du service IA
    private final AIAnalysisService aiService;

    // Endpoint GET
    @GetMapping("/analysis")
    public ApiResponse<AIResponse> analyze(){

        // Appelle le service qui va interroger FastAPI
        AIResponse aiResponse = aiService.analyzeSales();

        return new ApiResponse<>(
                true,
                aiResponse,
                "Analysis completed successfully",
                LocalDateTime.now()
        );
    }

}