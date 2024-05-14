package edu.shu.abs.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.shu.abs.constant.Role;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1293712947787864128L;

    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 角色{0=普通用户，1=管理员}
     */
    private Role role;

    /**
     * 账户是否被封禁
     */
    private Boolean isBanned;

    /**
     * 个人信息是否公开
     */
    private Boolean isInfoPublic;

    /**
     * 个人评价是否公开
     */
    private Boolean isCommentPublic;

    /**
     * 个人简介
     */
    private String introduction;

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
