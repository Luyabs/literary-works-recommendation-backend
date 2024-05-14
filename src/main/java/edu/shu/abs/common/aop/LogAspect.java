package edu.shu.abs.common.aop;

import edu.shu.abs.common.authentication.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Order(1)
@Aspect
@Component
public class LogAspect {
    @Pointcut("execution(* edu.shu.abs.service.*Service.*(..))")   // @annotation(Log)
    private void printLogMethod() {
        /*do nothing*/
    }

    @Before("printLogMethod()")
    public void invoke(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        String now = ""; // LocalDateTime.now().toString().split("\\.")[0];
        if (UserInfo.isLogin()) {
            long userId = UserInfo.getUserId();
            log.info(now //+ "\t"
                    + "user_id=" + userId + "\t"
                    + joinPoint.getTarget().getClass().getName() + "\t"
                    + methodSignature.getMethod().getName() + "\t"
                    + Arrays.asList(joinPoint.getArgs()));
        }
        else {
            log.info(now //+ "\t"
                    + "__guest__" + "\t"
                    + joinPoint.getTarget().getClass().getName() + "\t"
                    + methodSignature.getMethod().getName() + "\t"
                    + Arrays.asList(joinPoint.getArgs()));
        }
    }
}
