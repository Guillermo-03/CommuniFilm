package com.communifilm.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import com.communifilm.services.MovieReviewService;
import com.communifilm.services.MovieService;
import com.communifilm.models.MovieReview;
import com.communifilm.dtos.MovieDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tool for analyzing and summarizing movie reviews from the CommuniFilm community.
 * This tool provides the AI agent with capabilities to:
 * - Summarize reviews for a specific movie
 * - Analyze sentiment and themes in reviews
 * - Extract common opinions and feedback
 */
@Component
@RequiredArgsConstructor
public class MovieReviewTool implements AgentTool {

    private final MovieReviewService reviewService;
    private final MovieService movieService;
    private final ChatLanguageModel model;

    @Tool("Summarize and analyze reviews for a specific movie by movie ID. Returns a summary of pros, cons, and overall sentiment.")
    public String summarizeMovieReviews(Long movieId) {
        try {
            // Fetch reviews from Firestore
            List<MovieReview> reviews = reviewService.getReviewsForMovie(movieId);

            if (reviews.isEmpty()) {
                return "No reviews found for movie ID: " + movieId + ". This movie hasn't been reviewed by the CommuniFilm community yet.";
            }

            // Get movie details for context
            MovieDetailDto movieDetails = movieService.getMovieDetails(movieId.intValue());
            String movieTitle = movieDetails != null ? movieDetails.getTitle() : "Unknown Movie";

            // Combine all review texts
            String reviewTexts = reviews.stream()
                .map(MovieReview::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

            // Create prompt for the LLM
            String prompt = String.format(
                "Summarize the following %d reviews for the movie '%s' (ID: %d).\n\n" +
                "Provide:\n" +
                "1. Overall sentiment (positive/mixed/negative)\n" +
                "2. Top 3 pros mentioned by reviewers\n" +
                "3. Top 3 cons mentioned by reviewers\n" +
                "4. A brief overall summary\n\n" +
                "Reviews:\n%s",
                reviews.size(), movieTitle, movieId, reviewTexts
            );

            return model.generate(prompt);

        } catch (Exception e) {
            return "Error fetching reviews for movie ID " + movieId + ": " + e.getMessage();
        }
    }

    @Tool("Analyze the sentiment and common themes in reviews for a specific movie by movie ID")
    public String analyzeReviewSentiment(Long movieId) {
        try {
            List<MovieReview> reviews = reviewService.getReviewsForMovie(movieId);

            if (reviews.isEmpty()) {
                return "No reviews found for movie ID: " + movieId;
            }

            // Get movie details for context
            MovieDetailDto movieDetails = movieService.getMovieDetails(movieId.intValue());
            String movieTitle = movieDetails != null ? movieDetails.getTitle() : "Unknown Movie";

            String reviewTexts = reviews.stream()
                .map(MovieReview::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

            String prompt = String.format(
                "Analyze the sentiment and common themes in these %d reviews for '%s'.\n\n" +
                "Identify:\n" +
                "1. The overall sentiment distribution (what %% are positive, neutral, negative)\n" +
                "2. The most frequently mentioned themes or topics\n" +
                "3. Any recurring praise or complaints\n" +
                "4. The general tone of the community's response\n\n" +
                "Reviews:\n%s",
                reviews.size(), movieTitle, reviewTexts
            );

            return model.generate(prompt);

        } catch (Exception e) {
            return "Error analyzing reviews for movie ID " + movieId + ": " + e.getMessage();
        }
    }

    @Tool("Count the number of reviews for a specific movie by movie ID")
    public String getReviewCount(Long movieId) {
        try {
            List<MovieReview> reviews = reviewService.getReviewsForMovie(movieId);
            MovieDetailDto movieDetails = movieService.getMovieDetails(movieId.intValue());
            String movieTitle = movieDetails != null ? movieDetails.getTitle() : "Unknown Movie";

            return String.format("The movie '%s' (ID: %d) has %d review(s) from the CommuniFilm community.",
                movieTitle, movieId, reviews.size());

        } catch (Exception e) {
            return "Error counting reviews for movie ID " + movieId + ": " + e.getMessage();
        }
    }

    @Tool("Get all reviews posted by a specific user by their User ID. Returns which movies they reviewed and their review texts.")
    public String getUserReviews(String userId) {
        try {
            List<MovieReview> reviews = reviewService.getReviewsForUser(userId);

            if (reviews.isEmpty()) {
                return "This user hasn't posted any reviews yet on CommuniFilm.";
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("User has posted %d review(s):\n\n", reviews.size()));

            for (MovieReview review : reviews) {
                try {
                    MovieDetailDto movieDetails = movieService.getMovieDetails(review.getMovieId().intValue());
                    String movieTitle = movieDetails != null ? movieDetails.getTitle() : "Unknown Movie";

                    result.append(String.format("Movie: %s (ID: %d)\n", movieTitle, review.getMovieId()));
                    result.append(String.format("Review: %s\n\n", review.getText()));
                } catch (Exception e) {
                    result.append(String.format("Movie ID: %d\n", review.getMovieId()));
                    result.append(String.format("Review: %s\n\n", review.getText()));
                }
            }

            return result.toString();

        } catch (Exception e) {
            return "Error fetching reviews for user " + userId + ": " + e.getMessage();
        }
    }

    @Override
    public String getName() {
        return "Movie Review Tool";
    }

    @Override
    public String getDescription() {
        return "Analyzes and summarizes movie reviews from the CommuniFilm community stored in Firestore";
    }
}
