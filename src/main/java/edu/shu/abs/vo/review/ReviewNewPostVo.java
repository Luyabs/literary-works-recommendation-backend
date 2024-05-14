package edu.shu.abs.vo.review;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReviewNewPostVo {
    /**
     * 作品号
     */
    private Long workId;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 评论内容
     */
    private String content;
}
