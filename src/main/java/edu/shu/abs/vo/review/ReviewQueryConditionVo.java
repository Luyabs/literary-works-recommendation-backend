package edu.shu.abs.vo.review;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReviewQueryConditionVo {
    /**
     * 平均评分下限
     */
    private Double ratingLowerBound;

    /**
     * 平均评分上限
     */
    private Double ratingUpperBound;

    /**
     * 忽略无内容(仅打分)的评论
     */
    private Boolean ignoreNoContent;
}
