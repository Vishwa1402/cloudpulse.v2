package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.dto.AiChatRequest;
import com.cloudpulse.backend.dto.AiChatResponse;
import com.cloudpulse.backend.service.AiAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiChatControllerTest {

    @Mock
    private AiAnalysisService aiAnalysisService;

    private AiChatController aiChatController;

    @BeforeEach
    void setUp() {
        aiChatController = new AiChatController(aiAnalysisService);
    }

    @Test
    void chat_ValidQuery_ReturnsOkResponse() {
        AiChatRequest request = new AiChatRequest("Why is CPU spiked?");
        when(aiAnalysisService.handleTelemetryChat("Why is CPU spiked?")).thenReturn("Fact: CPU is 90% due to load.");

        ResponseEntity<AiChatResponse> response = aiChatController.chat(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Fact: CPU is 90% due to load.", response.getBody().getReply());
    }

    @Test
    void chat_EmptyQuery_ReturnsBadRequest() {
        AiChatRequest request = new AiChatRequest("");

        ResponseEntity<AiChatResponse> response = aiChatController.chat(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getReply().contains("Query must not be empty"));
    }
}
