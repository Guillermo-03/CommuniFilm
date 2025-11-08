package com.communifilm.ai;

/**
 * Defines the common interface for all tools that can be used by the AI agent.
 * A tool represents an action the agent can take to accomplish a task
 * (for example, analyzing movie reviews or calling the TMDB API).
 * Each tool must provide a short name and a one-line description.
 */
public interface AgentTool {
    /**
     * @return a short name for the tool
     */
    String getName();

    /**
     * @return a one-line description of what the tool does
     */
    String getDescription();
}
