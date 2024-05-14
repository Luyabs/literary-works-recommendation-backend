package edu.shu.abs.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 收藏夹
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Data
@Accessors(chain = true)
public class Collection implements Serializable {
    @Serial
    private static final long serialVersionUID = 1293712947787864128L;

    /**
     * 收藏夹id
     */
    @TableId(value = "collection_id", type = IdType.AUTO)
    private Long collectionId;

    /**
     * 所属用户id
     */
    private Long ownerId;

    /**
     * 收藏夹名
     */
    private String collectionName;

    /**
     * 收藏夹简介
     */
    private String introduction;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 是否为默认收藏夹(仅一个收藏夹可作为默认收藏夹)
     */
    private Boolean isDefaultCollection;

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
