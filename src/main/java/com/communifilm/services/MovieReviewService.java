package com.communifilm.services;

import com.communifilm.dtos.CreateReviewDto;
import com.communifilm.models.MovieReview;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class MovieReviewService {

    private final Firestore firestore;
    private final ReviewReplyService reviewReplyService;

    public MovieReviewService(Firestore firestore, ReviewReplyService reviewReplyService) {
        this.firestore = firestore;
        this.reviewReplyService = reviewReplyService;
    }

    public MovieReview createReview(CreateReviewDto reviewDto) throws ExecutionException, InterruptedException {
        // Get a reference to a new document
        DocumentReference reviewRef = firestore.collection("reviews").document();

        // Prepare the data with server timestamp placeholders
        Map<String, Object> newReviewData = new HashMap<>();
        newReviewData.put("reviewId", reviewRef.getId());
        newReviewData.put("movieId", reviewDto.getMovieId());
        newReviewData.put("userId", reviewDto.getUserId());
        newReviewData.put("text", reviewDto.getText());
        newReviewData.put("createdAt", FieldValue.serverTimestamp());
        newReviewData.put("updatedAt", FieldValue.serverTimestamp());

        // Write the data and wait for the operation to complete
        reviewRef.set(newReviewData).get();

        // Fetch the document we just created
        DocumentSnapshot snapshot = reviewRef.get().get();

        // Convert the complete snapshot (with timestamps) to a MovieReview object and return it
        return snapshot.toObject(MovieReview.class);
    }

    public List<MovieReview> getReviewsForMovie(Long movieId) throws ExecutionException, InterruptedException {
        List<MovieReview> reviews = new ArrayList<>();
        List<QueryDocumentSnapshot> documents = firestore.collection("reviews")
                .whereEqualTo("movieId", movieId)
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            MovieReview review = document.toObject(MovieReview.class);
            review.setReplyCount(reviewReplyService.countRepliesForReview(review.getReviewId()));
            reviews.add(review);
        }
        return reviews;
    }

    public List<MovieReview> getReviewsForUser(String userId) throws ExecutionException, InterruptedException {
        List<MovieReview> reviews = new ArrayList<>();
        List<QueryDocumentSnapshot> documents = firestore.collection("reviews")
                .whereEqualTo("userId", userId)
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            MovieReview review = document.toObject(MovieReview.class);
            review.setReplyCount(reviewReplyService.countRepliesForReview(review.getReviewId()));
            reviews.add(review);
        }
        return reviews;
    }
}