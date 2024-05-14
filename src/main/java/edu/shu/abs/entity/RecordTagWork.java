package edu.shu.abs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 作品标签
 * </p>
 *
 * @author abstraction
 * @since 2024-04-16 12:06:00
 */
@Data
@Accessors(chain = true)
@TableName("record_tag_work")
public class RecordTagWork implements Serializable {

    @Serial
    private static final long serialVersionUID = 1293712947787864128L;

    /**
     * 标签关系记录idid
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 作品id
     */
    private Long workId;


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
