package com.communifilm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyResponseDto {
    private String replyId;
    private String username;
    private String text;
    private Instant createdAt;
}
