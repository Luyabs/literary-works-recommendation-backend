package edu.shu.abs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.shu.abs.common.authentication.MyToken;
import edu.shu.abs.common.authentication.UserInfo;
import edu.shu.abs.common.base.BaseServiceImpl;
import edu.shu.abs.common.exception.exception.NoAccessException;
import edu.shu.abs.common.exception.exception.NotExistException;
import edu.shu.abs.common.exception.exception.ServiceException;
import edu.shu.abs.constant.Role;
import edu.shu.abs.entity.User;
import edu.shu.abs.mapper.UserMapper;
import edu.shu.abs.service.UserService;
import edu.shu.abs.vo.user.UserInfoVo;
import edu.shu.abs.vo.user.UserLoginVo;
import edu.shu.abs.vo.user.UserPrivacyVo;
import edu.shu.abs.vo.user.UserRegisterVo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {
    @Value("${token.timeout}")
    private Long tokenTimeOut;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String login(UserLoginVo userLoginVo) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("username", userLoginVo.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new NotExistException("用户不存在");
        }
        if (user.getIsBanned()) {
            throw new NoAccessException("你的账号已于" + user.getUpdateTime() + "被封禁");
        }
        if (!userLoginVo.getPassword().equals(user.getPassword())) {
            throw new ServiceException("密码错误");
        }
        // 生成UUID-token并写入redis {token: {user_id: 123; role: 0}}
        String token = MyToken.generateToken();
        final String REDIS_TOKEN_KEY = "token4auth:" + token;
        redisTemplate.opsForHash().put(REDIS_TOKEN_KEY, "user_id", user.getUserId());
        redisTemplate.opsForHash().put(REDIS_TOKEN_KEY, "role", user.getRole().getKey());
        redisTemplate.expire(REDIS_TOKEN_KEY, tokenTimeOut, TimeUnit.HOURS);
        return token;
    }

    @SneakyThrows
    @Override
    public UserInfoVo info(String token) {
        User user = getByIdNotNull(UserInfo.getUserId());
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(user, userInfoVo);     // 将user的属性赋值给vo
        userInfoVo.setRoles(List.of(user.getRole().getValue()));    // 将role属性从Role转到List<String>
        return userInfoVo;
    }

    @Override
    public boolean register(UserRegisterVo userRegisterVo) {
        User user = new User().setRole(Role.NORMAL_USER);
        BeanUtils.copyProperties(userRegisterVo, user);
        if (user.getPassword().length() < 6)
            throw new ServiceException("密码长度需要大于6位");
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean logout() {
        // 从redis中移除token
        return Boolean.TRUE.equals(redisTemplate.delete("token4auth:" + UserInfo.getToken()));
    }

    @Override
    public boolean updateInformation(UserRegisterVo userRegisterVo) {
        long userId = UserInfo.getUserId();
        User user = userMapper.selectById(userId);

        if (ObjectUtils.isNotEmpty(userRegisterVo.getUsername())
                && !user.getUsername().equals(userRegisterVo.getUsername()))
            user.setUsername(userRegisterVo.getUsername());
        if (ObjectUtils.isNotEmpty(userRegisterVo.getPassword())
                && !user.getPassword().equals(userRegisterVo.getPassword()))
            user.setPassword(userRegisterVo.getPassword());
        if (ObjectUtils.isNotEmpty(userRegisterVo.getIntroduction()))
            user.setIntroduction(userRegisterVo.getIntroduction());
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean updatePrivacy(UserPrivacyVo userPrivacyVo) {
        long userId = UserInfo.getUserId();
        User user = new User().setUserId(userId);
        BeanUtils.copyProperties(userPrivacyVo, user);
        return userMapper.updateById(user) > 0;
    }

    /**
     * 此方法只用于查询他人隐私设置
     */
    @Override
    public UserPrivacyVo getOtherPrivacySetting(Long userId) {
        if (userId.equals(UserInfo.getUserId()))    // 如果查询的用户是自己 则全部公开
            return new UserPrivacyVo().setIsCommentPublic(true).setIsInfoPublic(true);
        User user = getByIdNotNull(userId);
        UserPrivacyVo userPrivacyVo = new UserPrivacyVo();
        BeanUtils.copyProperties(user, userPrivacyVo);
        return userPrivacyVo;
    }

    @Override
    public UserInfoVo getOtherUserInfo(Long userId) {
        User user = getByIdNotNull(userId);         // 由BaseService提供的方法
        if (!userId.equals(UserInfo.getUserId()) && !user.getIsInfoPublic()) {
            throw new NoAccessException("该用户未公开自己的其他信息");
        }
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(user, userInfoVo);     // 将user的属性赋值给vo
        userInfoVo.setRoles(List.of(user.getRole().getValue()));    // 将role属性从int转到List<String>
        return userInfoVo;
    }

    @Override
    public Long getUserIdByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null)
            throw new NotExistException("用户名为" + username + "的用户不存在");
        return user.getUserId();
    }

    @Override
    public Boolean reverseUserBanStatus(Long userId) {
        final String REDIS_BLACKLIST_KEY = "blacklist4auth";
        if (!UserInfo.isAdmin())
            throw new NoAccessException("只有管理员才能使用此接口");

        User user = getByIdNotNull(userId);

        if (user.getRole().equals(Role.ADMIN))
            throw new NoAccessException("不能封禁管理员");

        boolean newBannedStatus = !user.getIsBanned();
        if (newBannedStatus) { // 在redis中添加该用户到黑名单
            redisTemplate.opsForSet().add(REDIS_BLACKLIST_KEY, userId);
            redisTemplate.expire(REDIS_BLACKLIST_KEY, tokenTimeOut, TimeUnit.HOURS);
        }
        else {    // 在redis中将该用户移出黑名单
            redisTemplate.opsForSet().remove(REDIS_BLACKLIST_KEY, userId);
        }
        user.setIsBanned(newBannedStatus);
        userMapper.updateById(user);
        return newBannedStatus;
    }
}
