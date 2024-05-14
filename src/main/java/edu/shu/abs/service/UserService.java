package edu.shu.abs.service;

import edu.shu.abs.common.base.BaseService;
import edu.shu.abs.entity.User;
import edu.shu.abs.vo.user.UserInfoVo;
import edu.shu.abs.vo.user.UserLoginVo;
import edu.shu.abs.vo.user.UserPrivacyVo;
import edu.shu.abs.vo.user.UserRegisterVo;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
public interface UserService extends BaseService<User> {
    String login(UserLoginVo userLoginVo);

    UserInfoVo info(String token);

    boolean register(UserRegisterVo user);

    boolean logout();

    boolean updateInformation(UserRegisterVo user);

    boolean updatePrivacy(UserPrivacyVo user);

    UserPrivacyVo getOtherPrivacySetting(Long userId);

    UserInfoVo getOtherUserInfo(Long userId);

    Long getUserIdByUsername(String username);

    Boolean reverseUserBanStatus(Long userId);

}
