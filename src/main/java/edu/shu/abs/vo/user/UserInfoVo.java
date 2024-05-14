package edu.shu.abs.vo.user;

import edu.shu.abs.entity.User;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户基本信息VO
 * 仅为token解析服务
 */
@Data
@Accessors(chain = true)
public class UserInfoVo extends User {
    /**
     * 角色
     */
    private List<String> roles;

    /**
     * 头像
     */
    private String avatar = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";
}
