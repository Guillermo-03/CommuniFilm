package com.communifilm.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import com.communifilm.services.MovieService;
import com.communifilm.dtos.MovieDetailDto;
import com.communifilm.dtos.MovieDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tool for interacting with the TMDB (The Movie Database) API.
 * This tool allows the AI agent to:
 * - Get detailed information about specific movies
 * - Search for movies by title or keywords
 * - Retrieve currently trending movies
 */
@Component
@RequiredArgsConstructor
public class TmdbTool implements AgentTool {

    private final MovieService movieService;
    private final ChatLanguageModel model;

    @Tool("Get detailed information about a specific movie by its TMDB ID")
    public String getMovieDetails(int movieId) {
        try {
            MovieDetailDto details = movieService.getMovieDetails(movieId);

            if (details == null) {
                return "Movie not found with ID: " + movieId;
            }

            String prompt = String.format(
                "Present this movie information in a friendly, conversational way:\n\n" +
                "Title: %s\n" +
                "Release Date: %s\n" +
                "Rating: %.1f/10\n" +
                "Overview: %s\n\n" +
                "Format it as if you're telling someone about this movie.",
                details.getTitle(),
                details.getReleaseDate() != null ? details.getReleaseDate() : "Unknown",
                details.getVoteAverage(),
                details.getOverview() != null ? details.getOverview() : "No description available"
            );

            return model.generate(prompt);

        } catch (Exception e) {
            return "Error fetching details for movie ID " + movieId + ": " + e.getMessage();
        }
    }

    @Tool("Search for movies by title or keywords in the TMDB database")
    public String searchMovies(String query) {
        try {
            List<MovieDto> results = movieService.searchMovies(query);

            if (results.isEmpty()) {
                return String.format("No movies found matching '%s'. Please try different search terms.", query);
            }

            // Limit to top 10 results for better readability
            List<MovieDto> topResults = results.stream()
                .limit(10)
                .collect(Collectors.toList());

            StringBuilder moviesList = new StringBuilder();
            moviesList.append(String.format("Found %d movie(s) matching '%s':\n\n",
                Math.min(results.size(), 10), query));

            for (int i = 0; i < topResults.size(); i++) {
                MovieDto movie = topResults.get(i);
                moviesList.append(String.format(
                    "%d. **%s** (ID: %d)\n   %s\n\n",
                    i + 1,
                    movie.getTitle(),
                    movie.getId(),
                    movie.getOverview() != null && !movie.getOverview().isEmpty()
                        ? movie.getOverview()
                        : "No description available"
                ));
            }

            if (results.size() > 10) {
                moviesList.append(String.format("... and %d more results", results.size() - 10));
            }

            return moviesList.toString();

        } catch (Exception e) {
            return "Error searching for movies: " + e.getMessage();
        }
    }

    @Tool("Get a list of currently trending movies this week")
    public String getTrendingMovies() {
        try {
            List<MovieDto> trendingMovies = movieService.getTrendy();

            if (trendingMovies.isEmpty()) {
                return "Unable to fetch trending movies at this time. Please try again later.";
            }

            StringBuilder moviesList = new StringBuilder();
            moviesList.append("Here are this week's trending movies:\n\n");

            // Show top 10 trending movies
            List<MovieDto> topTrending = trendingMovies.stream()
                .limit(10)
                .collect(Collectors.toList());

            for (int i = 0; i < topTrending.size(); i++) {
                MovieDto movie = topTrending.get(i);
                moviesList.append(String.format(
                    "%d. **%s** (ID: %d)\n   %s\n\n",
                    i + 1,
                    movie.getTitle(),
                    movie.getId(),
                    movie.getOverview() != null && !movie.getOverview().isEmpty()
                        ? movie.getOverview()
                        : "No description available"
                ));
            }

            return moviesList.toString();

        } catch (Exception e) {
            return "Error fetching trending movies: " + e.getMessage();
        }
    }

    @Tool("Provide a quick summary comparing multiple movies by their IDs")
    public String compareMovies(int[] movieIds) {
        try {
            if (movieIds.length < 2) {
                return "Please provide at least 2 movie IDs to compare.";
            }

            if (movieIds.length > 5) {
                return "Please provide no more than 5 movie IDs to compare.";
            }

            StringBuilder moviesInfo = new StringBuilder();
            moviesInfo.append("Movies to compare:\n\n");

            for (int movieId : movieIds) {
                MovieDetailDto details = movieService.getMovieDetails(movieId);
                if (details != null) {
                    moviesInfo.append(String.format(
                        "- **%s** (ID: %d)\n" +
                        "  Release: %s | Rating: %.1f/10\n" +
                        "  Overview: %s\n\n",
                        details.getTitle(),
                        details.getId(),
                        details.getReleaseDate() != null ? details.getReleaseDate() : "Unknown",
                        details.getVoteAverage(),
                        details.getOverview() != null ? details.getOverview() : "No description available"
                    ));
                }
            }

            String prompt = String.format(
                "Compare and contrast these movies:\n\n%s\n\n" +
                "Highlight:\n" +
                "1. Similarities and differences in themes or genres\n" +
                "2. Which movie might appeal to different types of viewers\n" +
                "3. Notable differences in ratings and popularity",
                moviesInfo.toString()
            );

            return model.generate(prompt);

        } catch (Exception e) {
            return "Error comparing movies: " + e.getMessage();
        }
    }

    @Override
    public String getName() {
        return "TMDB Tool";
    }

    @Override
    public String getDescription() {
        return "Searches and retrieves movie information from The Movie Database (TMDB) API";
    }
}
