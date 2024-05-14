package edu.shu.abs.common.authentication;

import edu.shu.abs.common.exception.exception.NotExistException;
import edu.shu.abs.constant.Role;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {
    private static final ThreadLocal<Map<String, Object>> userThreadLocal = new ThreadLocal<>();

    public static void set(Long userId, Role role, String token) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("role", role);
        map.put("token", token);
        userThreadLocal.set(map);
    }

    public static void remove() {
        userThreadLocal.remove();
    }

    public static Map<String, Object> getThreadLocalMap() {
        proveLogin();
        return userThreadLocal.get();
    }

    /**
     * 获取用户id
     * @return 用户id
     */
    public static Long getUserId() {
        return (Long) UserInfo.getThreadLocalMap().get("userId");
    }

    /**
     * 获取用户身份
     * @return 用户身份
     */
    public static Role getRole() {
        proveLogin();
        return (Role) UserInfo.getThreadLocalMap().get("role");
    }

    /**
     * 获取登录令牌 token
     * @return token
     */
    public static String getToken() {
        proveLogin();
        return (String) UserInfo.getThreadLocalMap().get("token");
    }


    /**
      * 是否管理员
      * @return 管理 / 普通用户
      */
    public static boolean isAdmin() {
        proveLogin();
        return UserInfo.getRole() == Role.ADMIN;
    }

    /**
     * 是否登录
     * @return 登录 / 未登录
     */
    public static boolean isLogin() {
        return userThreadLocal.get() != null;
    }

    /**
     * 保证处于登录状态
     */
    private static void proveLogin() {
        if (!isLogin())
            NotExistException.throwException("用户未登录");
    }


}

