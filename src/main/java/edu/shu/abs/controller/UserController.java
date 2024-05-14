package edu.shu.abs.controller;

import edu.shu.abs.common.Result;
import edu.shu.abs.service.UserService;
import edu.shu.abs.vo.user.UserInfoVo;
import edu.shu.abs.vo.user.UserLoginVo;
import edu.shu.abs.vo.user.UserPrivacyVo;
import edu.shu.abs.vo.user.UserRegisterVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(tags = "身份认证", value = "登录", notes = "传入username和password")
    @PostMapping("/login")
    Result login(@RequestBody UserLoginVo user) {
        String token = userService.login(user);
        return Result.success().data("token", token);
    }

    @ApiOperation(tags = "身份认证", value = "解析token", notes = "传入token")
    @GetMapping("/info")
    public Result getInfo(@RequestParam("token") String token) {
        UserInfoVo info = userService.info(token);
        return Result.success().data("user", info);
    }

    @ApiOperation(tags = "身份认证", value = "注册")
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterVo user) {
        boolean res = userService.register(user);
        return res ? Result.success().message("注册成功") : Result.error();
    }

    @ApiOperation(tags = "身份认证", value = "登出")
    @PostMapping("/logout")
    public Result logout() {
        boolean res = userService.logout();
        return res ? Result.success().message("登出成功") : Result.error();
    }

    @ApiOperation(tags = "个人信息管理", value = "修改个人信息")
    @PutMapping("/information")
    public Result changeInformation(@RequestBody UserRegisterVo user) {
        boolean res = userService.updateInformation(user);
        return res ? Result.success().message("个人信息更新成功") : Result.error();
    }

    @ApiOperation(tags = "个人信息管理", value = "设置个人信息公开状态")
    @PutMapping("/privacy")
    public Result changePrivacy(@RequestBody UserPrivacyVo user) {
        boolean res = userService.updatePrivacy(user);
        return res ? Result.success().message("个人信息公开状态更新成功") : Result.error();
    }


    @ApiOperation(tags = "访问他人信息", value = "隐私设置查询")
    @GetMapping("/visit/privacy/{userId}")
    public Result searchPrivacySetting(@PathVariable Long userId) {
        UserPrivacyVo privacy = userService.getOtherPrivacySetting(userId);
        return Result.success().data("privacy", privacy);
    }

    @ApiOperation(tags = "访问他人信息", value = "访问个人信息")
    @GetMapping("/visit/info/{userId}")
    public Result getInfo(@PathVariable Long userId) {
        UserInfoVo userInfoVo = userService.getOtherUserInfo(userId);
        return Result.success().data("user", userInfoVo);
    }

    @ApiOperation(tags = "访问他人信息", value = "通过用户名查找用户ID")
    @GetMapping("/visit/get_id/{username}")
    public Result getUserIdByUsername(@PathVariable String username) {
        Long userId = userService.getUserIdByUsername(username);
        return Result.success().data("userId", userId);
    }

    @ApiOperation(tags = "管理员专用", value = "封禁用户")
    @PostMapping("/ban/{userId}")
    public Result reverseUserBanStatus(@PathVariable Long userId) {
        Boolean newBannedStatus = userService.reverseUserBanStatus(userId);
        return Result.success().data("isBanned", newBannedStatus);
    }
}
