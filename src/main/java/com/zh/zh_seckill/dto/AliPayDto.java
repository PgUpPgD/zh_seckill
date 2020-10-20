package com.zh.zh_seckill.dto;

import lombok.Data;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-27 16:30
 */
@Data
public class AliPayDto {
    private String out_trade_no;    //订单号 不重复
//    private String product_code="FAST_INSTANT_TRADE_PAY"; //商品销售码  统一收单下单并支付页面接口必选
    private Double total_amount;    //订单总金额，单位为元，精确到小数点后两位
    private String subject;         //订单标题
    private Double refund_amount;   //退款金额
    private Double out_request_no;  //退款请求号
}