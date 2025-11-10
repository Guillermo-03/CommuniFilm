package com.communifilm.controllers;

import com.communifilm.dtos.CreateReviewReplyDto;
import com.communifilm.dtos.ReviewReplyResponseDto;
import com.communifilm.models.ReviewReply;
import com.communifilm.services.ReviewReplyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReviewReplyController {
    private ReviewReplyService reviewReplyService;

    public ReviewReplyController(ReviewReplyService service){
        this.reviewReplyService = service;
    }


    @PostMapping
    public ReviewReply createReply(@RequestBody CreateReviewReplyDto replyDto) throws ExecutionException, InterruptedException {
        return reviewReplyService.createReply(replyDto);
    }

    @GetMapping("/replies/{reviewId}")
    public List<ReviewReplyResponseDto> getRepliesForReview(@PathVariable String reviewId) throws ExecutionException, InterruptedException{
        return reviewReplyService.getRepliesForReview(reviewId);
    }

}
