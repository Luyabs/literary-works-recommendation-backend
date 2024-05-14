package edu.shu.abs.common.exception.handler;

import edu.shu.abs.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.ConnectException;
import java.security.SignatureException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局异常处理 - 全局性质问题
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, SignatureException.class})
    private Result argumentOrSomethingError(Exception ex) {
//        ex.printStackTrace();
        log.error("[" + ex.getClass() + "]" + ex.getMessage());
        return Result.error().message(ex.getMessage());
    }

    /**
     * 属性不对
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private Result jsonParseErrorException(Exception ex) {
        String message = ex.getMessage();
        if (ex.getMessage().trim().startsWith("JSON parse error"))
            message = ex.getMessage().split(": ")[1].split(";")[0];
        else if (ex.getMessage().trim().startsWith("Required request body is missing"))
            message = "需要以JSON格式传入参数";
        else
            ex.printStackTrace();   // 未知错误
        log.error("[HttpMessageNotReadableException] " + message);
        return Result.error().message(message);
    }

    /**
     * 空指针异常 应当直接处理
     */
    @ExceptionHandler(NullPointerException.class)
    private Result nullPointerException(Exception ex) {
        ex.printStackTrace();   // 未知错误, 应当直接进行处理
        return Result.error().message("空指针异常, 联系后端修复bug");
    }

    /**
     * 400 BAD REQUEST (另一后端自定义的返回结果)
     */
    @ExceptionHandler(HttpClientErrorException.class)
    private Result badRequest400(Exception ex) {
        String message = ex.getMessage();
        if (message.startsWith("400 BAD REQUEST: \"{")) {
            String patternString = "\\{.*?}";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(message);
            if (matcher.find())
                message = matcher.group(0);
        }
        log.error("[HttpClientErrorException] " + message);
        return Result.error().message(message);
    }

    /**
     * 连接异常
     */
    @ExceptionHandler(ConnectException.class)
    private Result connectException(Exception ex) {
        String message = ex.getMessage();
        if (message.startsWith("I/O error on")) {
            log.error("[ConnectException] " + message);
            return Result.error().message("后端无法通过HTTP请求访问其他服务, 请等待后端开启服务");
        } else {
            ex.printStackTrace();
            return Result.error().message(message);
        }
    }

    /**
     * 连接资源异常
     */
    @ExceptionHandler(ResourceAccessException.class)
    private Result resourceAccessException(Exception ex) {
        String message = ex.getMessage();
        if (message.contains("Read timed out")) {
            log.info("[ResourceAccessException] " + message);
            return Result.success().message("该接口耗时较久, 请耐心等待完成");
        } else if (message.contains("Connection refused")) {
            log.warn("[ResourceAccessException] " + message);
            return Result.error().message("后端无法通过HTTP请求访问其他服务, 请等待后端开启服务");
        } else {
            ex.printStackTrace();
            return Result.error().message(message);
        }
    }

    /**
     * 传入参数错误
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private Result methodArgumentTypeMismatchException(Exception ex) {
        String message = ex.getMessage();
        if (message.startsWith("Failed to convert value of type ")) {
            message = message.split("type '")[2].split("';")[0];
            log.info("[MethodArgumentTypeMismatchException] " + message);
            return Result.error().message("传入参数类型错误, 需要传入" + message);
        } else {
            ex.printStackTrace();
            return Result.error().message(message);
        }
    }
}
