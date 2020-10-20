package com.zh.zh_seckill.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SeckillGoodsVo {
    private Integer skillId;
    private String sName;
    private Double gPrice;   //商品原价
    private Integer sStock;
    private String sImg;
    private Double sPrice;  //商品秒杀价
    private Date bTime;
    private Date eTime;

}
