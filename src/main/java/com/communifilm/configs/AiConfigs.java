package com.communifilm.configs;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for AI-related beans.
 * This class sets up the ChatLanguageModel (OpenAI) that will be used
 * by all AI tools in the application.
 */
@Configuration
public class AiConfigs {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    /**
     * Creates a ChatLanguageModel bean using OpenAI's GPT model.
     * This model is injected into all AI tools for generating responses.
     *
     * @return ChatLanguageModel instance configured for OpenAI
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        // Check if API key is configured
        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            throw new IllegalStateException(
                "OpenAI API key is not configured. " +
                "Please set the OPENAI_API_KEY environment variable or " +
                "add openai.api.key to application.properties"
            );
        }

        return OpenAiChatModel.builder()
            .apiKey(openaiApiKey)
            .modelName("gpt-4o-mini")  // Using cost-effective mini model
            .temperature(0.7)           // Balanced creativity/consistency
            .maxTokens(1000)            // Limit response length
            .build();
    }
}
