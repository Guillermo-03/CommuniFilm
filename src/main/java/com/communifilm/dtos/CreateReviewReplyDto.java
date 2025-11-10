package com.communifilm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewReplyDto {
    private String parentReviewId;
    private Long movieId;
    private String userId;
    private String text;
}
