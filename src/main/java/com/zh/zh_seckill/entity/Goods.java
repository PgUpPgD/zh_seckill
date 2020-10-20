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
public class Goods implements Serializable {
    private Integer goodsId;    //商品id
    private String goodsName;   //商品名称
    private Double goodsPrice;  //商品价格
    private int goodsStock;     //商品库存
    private String goodsImg;    //商品图片
}
