package com.zh.zh_seckill.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: Skill
 * @description: 秒杀商品类
 * @author: Feri
 * @create: 2020-02-25 10:29
 */
@Data
public class SkillGoods implements Serializable {
    private Integer seckillId; //秒杀商品的id
    private Date beginTime; //开始时间
    private Date endTime;//结束时间
    private int seckillStock;//秒杀商品的库存量
    private Integer goodsId;
    private Double seckillPrice;
    private String seckillUrl;
    private Integer version;
}