package com.communifilm.services;

import com.communifilm.dtos.CreateReviewReplyDto;
import com.communifilm.dtos.ReviewReplyResponseDto;
import com.communifilm.models.ReviewReply;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ReviewReplyService {

    private final Firestore firestore;

    public ReviewReplyService(Firestore firestore) {
        this.firestore = firestore;
    }

    public ReviewReply createReply(CreateReviewReplyDto replyDto) throws ExecutionException, InterruptedException {
        DocumentReference replyRef = firestore.collection("reviewReplies").document();

        Map<String, Object> newReplyData = new HashMap<>();
        newReplyData.put("replyId", replyRef.getId());
        newReplyData.put("parentReviewId", replyDto.getParentReviewId());
        newReplyData.put("movieId", replyDto.getMovieId());
        newReplyData.put("userId", replyDto.getUserId());
        newReplyData.put("username", replyDto.getUsername());
        newReplyData.put("text", replyDto.getText());
        newReplyData.put("createdAt", FieldValue.serverTimestamp());
        newReplyData.put("updatedAt", FieldValue.serverTimestamp());

        replyRef.set(newReplyData).get();

        DocumentSnapshot snapshot = replyRef.get().get();
        return snapshot.toObject(ReviewReply.class);
    }

    public List<ReviewReplyResponseDto> getRepliesForReview(String reviewId)
            throws ExecutionException, InterruptedException {

        List<ReviewReplyResponseDto> replies = new ArrayList<>();

        List<QueryDocumentSnapshot> documents = firestore.collection("reviewReplies")
                .whereEqualTo("parentReviewId", reviewId)
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            ReviewReply reply = document.toObject(ReviewReply.class);

            replies.add(
                    ReviewReplyResponseDto.builder()
                            .replyId(reply.getReplyId())
                            .parentReviewId(reply.getParentReviewId())
                            .username(reply.getUsername())
                            .text(reply.getText())
                            .createdAt(reply.getCreatedAt())
                            .build()
            );
        }

        return replies;
    }

    public int countRepliesForReview(String reviewId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestore.collection("reviewReplies")
                .whereEqualTo("parentReviewId", reviewId)
                .get()
                .get()
                .getDocuments();

        return documents.size();
    }
}
