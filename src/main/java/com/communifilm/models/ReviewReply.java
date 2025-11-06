package com.communifilm.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReply{
    private String replyID;
    private String parentReviewId;
    private Long movieId;
    private String userId;
    private String text;
    private Instant createdAt;
    private Instant updatedAt;}
