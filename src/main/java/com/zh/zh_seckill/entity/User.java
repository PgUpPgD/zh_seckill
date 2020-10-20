package com.zh.zh_seckill.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 10:29
 */
@Data
public class User implements Serializable {
    private Integer uid;
    private String phone;
    private String password;
//    private String salt;  //盐
}