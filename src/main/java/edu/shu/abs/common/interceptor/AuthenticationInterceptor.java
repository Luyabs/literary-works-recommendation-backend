package edu.shu.abs.common.interceptor;

import com.alibaba.fastjson2.JSON;
import edu.shu.abs.common.Result;
import edu.shu.abs.common.authentication.UserInfo;
import edu.shu.abs.common.exception.exception.ServiceException;
import edu.shu.abs.constant.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token处理拦截器
 */
@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行跨域信息 OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        try {
            String token = request.getHeader("token");
            String redisTokenKey = "token4auth:" + token;
            // 检查token是否存在
            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisTokenKey))) {
                throw new ServiceException("token不存在或错误");
            }

            // 检查字段是否存在
            Object rawUserId = redisTemplate.opsForHash().get(redisTokenKey, "user_id");
            Object rawRole = redisTemplate.opsForHash().get(redisTokenKey, "role");
            if (rawUserId == null || rawRole == null) {
                throw new ServiceException("token属性不完整");
            }

            // 检查用户是否在黑名单中 (黑名单将拦截用户通过现有的token登录)
            Boolean inBlackList = redisTemplate.opsForSet().isMember("blacklist4auth", rawUserId);
            if (Boolean.TRUE.equals(inBlackList)) {
                redisTemplate.delete(redisTokenKey);
                throw new ServiceException("用户处于被封禁状态");
            }

            // 写入UserInfo (ThreadLocal)
            Long userId = Long.parseLong(rawUserId.toString());
            Role role = Role.getRole(Integer.parseInt(rawRole.toString()));
            UserInfo.set(userId, role, token);   // 写入userId到线程副本

        }
        catch (Exception e) {     // 拦截异常token 多半是空token或错误token
            log.error("[Login Interceptor] " + e.getMessage());
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(Result.error().message(e.getMessage()).code(401)));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserInfo.remove();
    }
}
