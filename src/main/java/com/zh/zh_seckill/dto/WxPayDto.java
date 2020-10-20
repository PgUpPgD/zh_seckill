package com.zh.zh_seckill.dto;

import lombok.Data;

@Data
public class WxPayDto {
    private String body;
    private String out_trade_no;
    private int total_fee;//单位分
}
