package com.communifilm.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import com.communifilm.services.UserService;
import com.communifilm.services.MovieService;
import com.communifilm.models.User;
import com.communifilm.models.FavoriteMovie;
import com.communifilm.dtos.MovieDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tool for providing movie recommendations based on user preferences and search criteria.
 * This tool enables the AI agent to:
 * - Recommend movies based on a user's favorite movies
 * - Suggest movies matching specific criteria or keywords
 * - Provide personalized recommendations
 */
@Component
@RequiredArgsConstructor
public class MovieRecommendationTool implements AgentTool {

    private final UserService userService;
    private final MovieService movieService;
    private final ChatLanguageModel model;

    @Tool("Recommend movies based on a user's top favorite movies. Requires the user's UID.")
    public String recommendBasedOnFavorites(String userId) {
        try {
            User user = userService.getUser(userId);

            if (user == null) {
                return "User not found with ID: " + userId;
            }

            List<FavoriteMovie> favorites = user.getTopMovies();

            if (favorites == null || favorites.isEmpty()) {
                return String.format("User '%s' hasn't set any favorite movies yet. " +
                    "They should add their top 3 favorite movies to get personalized recommendations!",
                    user.getDisplayName() != null ? user.getDisplayName() : user.getEmail());
            }

            // Build information about favorite movies
            StringBuilder favoritesInfo = new StringBuilder();
            favoritesInfo.append("User's top favorite movies:\n");
            for (FavoriteMovie fav : favorites) {
                favoritesInfo.append(String.format(
                    "%d. %s (ID: %d) - Released: %s, Rating: %.1f\n   Overview: %s\n\n",
                    fav.getRank(),
                    fav.getTitle(),
                    fav.getMovieId(),
                    fav.getReleaseDate(),
                    fav.getVoteAverage(),
                    fav.getOverview()
                ));
            }

            String prompt = String.format(
                "Based on these favorite movies, recommend 5 similar movies that the user might enjoy.\n\n" +
                "%s\n" +
                "For each recommendation:\n" +
                "1. Explain why it's similar to their favorites\n" +
                "2. Mention the movie title\n" +
                "3. Give a brief description\n\n" +
                "Focus on similar themes, genres, directors, or styles.",
                favoritesInfo.toString()
            );

            return model.generate(prompt);

        } catch (Exception e) {
            return "Error generating recommendations for user " + userId + ": " + e.getMessage();
        }
    }

    @Tool("Search and recommend movies matching specific criteria or keywords (e.g., 'action movies', 'romantic comedy', 'sci-fi thriller')")
    public String recommendByCriteria(String criteria) {
        try {
            // Search TMDB for movies matching the criteria
            List<MovieDto> searchResults = movieService.searchMovies(criteria);

            if (searchResults.isEmpty()) {
                // Try getting trending movies if search returns nothing
                searchResults = movieService.getTrendy();
                if (searchResults.isEmpty()) {
                    return "No movies found matching '" + criteria + "'. Please try different keywords.";
                }
                return "No exact matches found for '" + criteria + "', but here are some trending movies you might like:\n\n" +
                    formatMovieList(searchResults);
            }

            // Limit to top 10 results
            List<MovieDto> topResults = searchResults.stream()
                .limit(10)
                .collect(Collectors.toList());

            String moviesList = topResults.stream()
                .map(m -> String.format("- %s (ID: %d)\n  %s",
                    m.getTitle(),
                    m.getId(),
                    m.getOverview()))
                .collect(Collectors.joining("\n\n"));

            String prompt = String.format(
                "From these movies matching '%s', recommend the top 5 best matches.\n\n" +
                "Movies found:\n%s\n\n" +
                "For each recommendation, explain why it matches the criteria and what makes it worth watching.",
                criteria, moviesList
            );

            return model.generate(prompt);

        } catch (Exception e) {
            return "Error searching for movies matching '" + criteria + "': " + e.getMessage();
        }
    }

    @Tool("Get a list of currently trending movies")
    public String getTrendingMovies() {
        try {
            List<MovieDto> trendingMovies = movieService.getTrendy();

            if (trendingMovies.isEmpty()) {
                return "Unable to fetch trending movies at this time.";
            }

            return "Here are the currently trending movies:\n\n" + formatMovieList(trendingMovies);

        } catch (Exception e) {
            return "Error fetching trending movies: " + e.getMessage();
        }
    }

    /**
     * Helper method to format a list of movies into a readable string
     */
    private String formatMovieList(List<MovieDto> movies) {
        return movies.stream()
            .limit(10)
            .map(m -> String.format("- **%s** (ID: %d)\n  %s",
                m.getTitle(),
                m.getId(),
                m.getOverview() != null && !m.getOverview().isEmpty()
                    ? m.getOverview()
                    : "No description available"))
            .collect(Collectors.joining("\n\n"));
    }

    @Override
    public String getName() {
        return "Movie Recommendation Tool";
    }

    @Override
    public String getDescription() {
        return "Provides personalized movie recommendations based on user preferences and search criteria";
    }
}
