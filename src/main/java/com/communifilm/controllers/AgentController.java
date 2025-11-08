package com.communifilm.controllers;

import com.communifilm.services.MovieAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the AI movie agent.
 * Provides endpoints for users to interact with the AI assistant.
 */
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AgentController {

    private final MovieAgentService agentService;

    /**
     * Main chat endpoint for interacting with the AI agent.
     * If authenticated, maintains user-specific conversation history.
     * If not authenticated, uses default agent.
     *
     * @param request The chat request containing the user's message
     * @param authentication The authentication object (optional)
     * @return ChatResponse containing the agent's reply
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @RequestBody ChatRequest request,
            Authentication authentication) {

        log.info("Received chat request: {}", request.message());

        if (request.message() == null || request.message().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ChatResponse("Please provide a message."));
        }

        try {
            String response;

            // If user is authenticated, use user-specific agent
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName();
                log.debug("Authenticated user: {}", userId);
                response = agentService.chatWithUser(userId, request.message());
            } else {
                // Use default agent for unauthenticated requests
                log.debug("Using default agent for unauthenticated request");
                response = agentService.chat(request.message());
            }

            return ResponseEntity.ok(new ChatResponse(response));

        } catch (Exception e) {
            log.error("Error processing chat request", e);
            return ResponseEntity.internalServerError()
                .body(new ChatResponse("An error occurred while processing your request."));
        }
    }

    /**
     * Clears the conversation history for the current user.
     * If not authenticated, clears the default agent's memory.
     *
     * @param authentication The authentication object (optional)
     * @return Success message
     */
    @PostMapping("/clear")
    public ResponseEntity<MessageResponse> clearMemory(Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName();
                agentService.clearUserMemory(userId);
                log.info("Cleared memory for user: {}", userId);
                return ResponseEntity.ok(
                    new MessageResponse("Conversation history cleared successfully.")
                );
            } else {
                agentService.clearMemory();
                log.info("Cleared default agent memory");
                return ResponseEntity.ok(
                    new MessageResponse("Conversation history cleared successfully.")
                );
            }
        } catch (Exception e) {
            log.error("Error clearing memory", e);
            return ResponseEntity.internalServerError()
                .body(new MessageResponse("Error clearing conversation history."));
        }
    }

    /**
     * Health check endpoint for the agent service.
     *
     * @return Status information
     */
    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        try {
            int activeUsers = agentService.getActiveUserCount();
            return ResponseEntity.ok(
                new StatusResponse(
                    "Agent service is operational",
                    activeUsers,
                    true
                )
            );
        } catch (Exception e) {
            log.error("Error getting agent status", e);
            return ResponseEntity.ok(
                new StatusResponse(
                    "Agent service encountered an error",
                    0,
                    false
                )
            );
        }
    }

    // DTOs
    public record ChatRequest(String message) {}

    public record ChatResponse(String response) {}

    public record MessageResponse(String message) {}

    public record StatusResponse(
        String status,
        int activeUsers,
        boolean healthy
    ) {}
}
