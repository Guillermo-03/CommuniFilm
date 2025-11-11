package com.communifilm.controllers;

import com.communifilm.dtos.CreateReviewReplyDto;
import com.communifilm.dtos.ReviewReplyResponseDto;
import com.communifilm.models.ReviewReply;
import com.communifilm.services.ReviewReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/replies")
public class ReviewReplyController {
    private ReviewReplyService reviewReplyService;

    public ReviewReplyController(ReviewReplyService service){
        this.reviewReplyService = service;
    }


    @PostMapping
    public ReviewReply createReply(@RequestBody CreateReviewReplyDto replyDto) throws ExecutionException, InterruptedException {
        return reviewReplyService.createReply(replyDto);
    }

    @GetMapping("/review/{reviewId}")
    public List<ReviewReplyResponseDto> getRepliesForReview(@PathVariable String reviewId) throws ExecutionException, InterruptedException{
        return reviewReplyService.getRepliesForReview(reviewId);
    }

}
