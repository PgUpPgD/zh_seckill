package com.zh.zh_seckill.dto;

import com.zh.zh_seckill.entity.User;
import lombok.Data;

@Data
public class LoginTokenDto {
    private User user;
    private String token;
    private String verify; //验签
}
