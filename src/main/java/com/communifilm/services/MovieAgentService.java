package com.communifilm.services;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import com.communifilm.ai.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing the AI-powered movie agent.
 * This service initializes the agent with all available tools and provides
 * methods for interacting with it.
 *
 * The agent maintains separate chat memories for different users to provide
 * personalized, context-aware conversations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MovieAgentService {

    private final MovieReviewTool reviewTool;
    private final MovieRecommendationTool recommendationTool;
    private final TmdbTool tmdbTool;
    private final ChatLanguageModel chatLanguageModel;

    private MovieAgent defaultAgent;

    // Store separate chat memories for different users
    private final Map<String, ChatMemory> userMemories = new ConcurrentHashMap<>();
    private final Map<String, MovieAgent> userAgents = new ConcurrentHashMap<>();

    /**
     * Initializes the default agent after bean construction.
     * This agent is used for anonymous/unauthenticated requests.
     */
    @PostConstruct
    public void initialize() {
        log.info("Initializing MovieAgentService with tools: ReviewTool, RecommendationTool, TmdbTool");

        ChatMemory defaultMemory = MessageWindowChatMemory.withMaxMessages(20);

        this.defaultAgent = AiServices.builder(MovieAgent.class)
            .chatLanguageModel(chatLanguageModel)
            .tools(reviewTool, recommendationTool, tmdbTool)
            .chatMemory(defaultMemory)
            .build();

        log.info("MovieAgentService initialized successfully");
    }

    /**
     * Send a message to the agent and get a response.
     * Uses the default agent (no user context).
     *
     * @param userMessage The user's query
     * @return The agent's response
     */
    public String chat(String userMessage) {
        log.debug("Processing message: {}", userMessage);
        try {
            String response = defaultAgent.chat(userMessage);
            log.debug("Agent response generated successfully");
            return response;
        } catch (Exception e) {
            log.error("Error processing chat message", e);
            return "I apologize, but I encountered an error processing your request. Please try again.";
        }
    }

    /**
     * Send a message to the agent with user context.
     * Maintains separate conversation history for each user.
     *
     * @param userId The unique identifier for the user
     * @param userMessage The user's query
     * @return The agent's response
     */
    public String chatWithUser(String userId, String userMessage) {
        log.debug("Processing message from user {}: {}", userId, userMessage);

        try {
            // Get or create user-specific agent
            MovieAgent userAgent = userAgents.computeIfAbsent(userId, this::createUserAgent);

            // Inject user context into the message
            String contextualMessage = String.format(
                "[User ID: %s] %s",
                userId,
                userMessage
            );

            String response = userAgent.chat(contextualMessage);
            log.debug("Agent response generated successfully for user {}", userId);
            return response;

        } catch (Exception e) {
            log.error("Error processing chat message for user " + userId, e);
            return "I apologize, but I encountered an error processing your request. Please try again.";
        }
    }

    /**
     * Creates a new agent instance for a specific user with dedicated chat memory.
     *
     * @param userId The user's unique identifier
     * @return A new MovieAgent instance
     */
    private MovieAgent createUserAgent(String userId) {
        log.info("Creating new agent for user: {}", userId);

        ChatMemory userMemory = MessageWindowChatMemory.withMaxMessages(20);
        userMemories.put(userId, userMemory);

        return AiServices.builder(MovieAgent.class)
            .chatLanguageModel(chatLanguageModel)
            .tools(reviewTool, recommendationTool, tmdbTool)
            .chatMemory(userMemory)
            .build();
    }

    /**
     * Clears the conversation history for the default agent.
     */
    public void clearMemory() {
        log.info("Clearing default agent memory");
        initialize(); // Reinitialize with fresh memory
    }

    /**
     * Clears the conversation history for a specific user.
     *
     * @param userId The user whose conversation history should be cleared
     */
    public void clearUserMemory(String userId) {
        log.info("Clearing memory for user: {}", userId);

        ChatMemory memory = userMemories.get(userId);
        if (memory != null) {
            memory.clear();
        }

        // Remove the agent so it will be recreated with fresh memory next time
        userAgents.remove(userId);
        userMemories.remove(userId);
    }

    /**
     * Gets the number of active user sessions.
     *
     * @return The count of users with active chat sessions
     */
    public int getActiveUserCount() {
        return userAgents.size();
    }
}
