package edu.shu.abs.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户访问作品的历史记录
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Data
@Accessors(chain = true)
@TableName("history_user_work")
public class HistoryUserWork implements Serializable {

    @Serial
    private static final long serialVersionUID = 1293712947787864128L;

    /**
     * 访问记录id
     */
    @TableId(value = "history_id", type = IdType.AUTO)
    private Long historyId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 作品id
     */
    private Long workId;

    /**
     * 访问次数
     */
    private Integer visitCount;

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
