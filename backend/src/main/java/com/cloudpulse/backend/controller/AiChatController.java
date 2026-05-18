package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.dto.AiChatRequest;
import com.cloudpulse.backend.dto.AiChatResponse;
import com.cloudpulse.backend.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiChatController {

    private final AiAnalysisService aiAnalysisService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        if (request == null || request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new AiChatResponse("Query must not be empty."));
        }
        
        String reply = aiAnalysisService.handleTelemetryChat(request.getQuery());
        return ResponseEntity.ok(new AiChatResponse(reply));
    }
}
