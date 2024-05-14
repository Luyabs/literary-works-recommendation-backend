package edu.shu.abs.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录与注册VO
 */
@Data
@Accessors(chain = true)
public class UserRegisterVo extends UserLoginVo {
    /**
     * 用户简介
     */
    private String introduction;
}
