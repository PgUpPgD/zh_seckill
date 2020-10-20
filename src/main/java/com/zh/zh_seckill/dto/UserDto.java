package com.zh.zh_seckill.dto;

import lombok.Data;

@Data
public class UserDto {
    private String phone;
    private String pwd;
    private int type;  //0验证码，1密码
}
