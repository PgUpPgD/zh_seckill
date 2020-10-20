package com.zh.zh_seckill.dto;

import lombok.Data;

@Data
public class SkillDto {
    private Integer id;  //秒杀商品id；
    private Integer num; //数量
    private String url;  //动态路径标记
    private Integer uid;
    private Double price; //单价

}
