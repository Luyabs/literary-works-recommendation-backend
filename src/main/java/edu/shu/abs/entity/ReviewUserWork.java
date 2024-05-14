package edu.shu.abs.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 作品评论
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Data
@Accessors(chain = true)
@TableName("review_user_work")
public class ReviewUserWork implements Serializable {

    @Serial
    private static final long serialVersionUID = 1293712947787864128L;

    /**
     * 评论id
     */
    @TableId(value = "review_id", type = IdType.AUTO)
    private Long reviewId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 作品id
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

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
