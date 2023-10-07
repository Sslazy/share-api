package top.zxy.share.user.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import top.zxy.share.common.resp.CommonResp;
import top.zxy.share.user.domain.dto.LoginDTO;
import top.zxy.share.user.domain.entity.User;
import top.zxy.share.user.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/count")
    public CommonResp<Long> count(){
        Long count = userService.count();
        CommonResp<Long> commonResp = new CommonResp<>();
        commonResp.setData(count);
        return commonResp;
    }

    @PostMapping("/login")
    public CommonResp<User> login(@Valid  @RequestBody LoginDTO loginDTO){
        User user = userService.login(loginDTO);
        CommonResp<User> commonResp = new CommonResp<>();
        commonResp.setData(user);
        return commonResp;
    }
}
