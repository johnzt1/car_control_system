package com.heima.model.comment.dtos;

import lombok.Data;

@Data
public class CommentRepayDto {

    /**
     * 评论id
     */
    private String commentId;

    private Integer size;

    // 最小点赞数
    private Long minLikes;
}
