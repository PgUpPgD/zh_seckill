package com.zh.zh_seckill.controller;

import com.zh.zh_seckill.dto.UserDto;
import com.zh.zh_seckill.service.UserService;
import com.zh.zh_seckill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("api/user/sign")
    public R sign(@RequestBody UserDto dto){
        return userService.sign(dto);
    }

    @PostMapping("api/user/login")
    public R login(@RequestBody UserDto dto){
        return userService.login(dto);
    }


}
