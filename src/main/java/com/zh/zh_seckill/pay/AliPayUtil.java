package com.zh.zh_seckill.pay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.zh.zh_seckill.dto.AliPayDto;

/**
 * 基于支付宝 实现的常用支付封装
 */
public class AliPayUtil {
    private static AlipayClient alipayClient ;
    private static final String APP_ID="2017091608770636";
    private static final String APP_PRIVATE_KEY="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCh1qI8uo1qhrcePsa5JUAoYUX8HfPuBt7kc90aCP1M/v61/uzaU/lyGQeChKV3jdDTn2Lcq6kT5JBl3TLiaYHmO6cId1nQAIUxiT9zhB9crc4wAx8CVabMbcqUefs7Xsp+YhhUgU5X6GOS3emkUeL7RegBnL8vayfEBeUDgBxsk/K/VygBA8sapsEhnoOrB6bhMY4GaJrxb0kg9Ej8x4kpExLcxkT+UgcOiJvh6vpBZo5CJsiPQkFvSsNsWY2uSDudSL/KqpMxz+yPfVvZDt4fOfyi+CfYR43Jlo4tsT7joqH2JT06BH+KdJyc1D3Lqw7w/WdmZtmoLghH0kRZawrLAgMBAAECggEAYYtpm+rhQ7zQ8HTr+DogknYW5Z/0H5qai93d/Uw/yEHFqlJt1iZZKlE1upBS311l6beesdzxeuD/u7X4bokjV27K/YpaYsl9fl74FJslAApuRXgMH68aawsd2CIxsBYxPL3JZl3Np6SVJ7eDlJwakFMRRK+CeIVAoaDf6R01hKctkYnnE0wT+ffQNKWsISoEyiKVT3g5fur7iPOuDlDXsfi6Mm+e75wCXTmRRHmb8lPBAMLV+Kj5DFxg8dwNz81Fs4ZM2Aq0lBaTfy1H1zSlM1m42wcsMYDcgdEH9aq+OgqK+cny6umgs7/Alg7IgV/9b7AhKdvAqLy2ERUJtooj2QKBgQDeIoDW3HuTq7sBaBnu63f7icT2RM3fApfOiGM4UDtxPvc5dS5S//o3E8p+rbp21FfBeyLOJFd9dg/eu+ETA+63QMPw4Kq4AH/EA5AFohaOQ0IKFDjYyxfyD8ajA4USDwdiaW2/vmMeAtGSv+W5zWb9/t49LOTwzEW904+yOGcmhQKBgQC6guDZ0Ob4o9nx5XwZXEe2di4MupARHceGzmolyDvs3Qi/w+8QntrDvfqIJoqoxOG5NVi3jtjkqtJtMaPyxqNWTabWOOTLbrsqlvPUmeCl0j3FVFKAGcV7/b9XkLvh1DtnIe6rhhZCVB4e4bL/katpOTgulhmSMaWIaztGU0F1DwKBgQCTeobdn/6vuSlsMqhdFppPN1W8R0wDjt4o8iYlwibk9e//hswdsPN307zyQ/dzY2FsBIvEHx6zHkpFD6nMDSVVJzuv1gmiJjqtccwR4V5mT0MuG+TuElCwlkbD/ddAeRfm/6Ys0oNN7oMjkiI8LKH/alI0fXT2Zji7YhWaNpZNXQKBgEU6q0duWS1VdGJrcgLf0+aQO0uSPEN+MD+Dgrb/ee7TpJm5mpUqwb0CWWoMFE/MtJRQjtujdDJ8jZrmYBqPTLWOIS1G9PXl5idK3Lq/Wzlxrmf+gpj19+2sJEfWe0a5xkrjt3mHTd/U5VFFKXHfmiZ2jLoOEPPI5c6bLudNo/BVAoGBAMvwRxLO4xb11Ip4rnEHkw3Qn8lrddoC3/m7haHYZ5DyGe8wdCdEi6wyk5MvlNQdqdVg5bqV0AiotIBcd5Pemabun2WaB11h/6SSb6wKY4Fnz+H155zaEww4no9BTG9llqQV7H8AS77dN1bxhcpE/MGFoB9JFU0D+BwXAnth4z1u";
    private static final String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx7jJT+PSEM6ZiimTW0SGUfg4cJU04H/mQqkL2mk7KaHXFQqMh4US6xYkDlaEXzOOfxevuBqWOaB4/8TleO1CHZHXWHu9Xc+iYtJPNJGrxoGLM+6Cg9IafJTygRoaqdH0SoVMpxFdOpUftNdXHO+G0ZpS/7c1zpn8G64zN5J17IFrLcUlsEnSgOrJxsS2Q50b44er0KQlj76pehB2sTveHS2vdhqXzrv+oq99XtUKEY1a3nwDjXneI7YYKLHD9KU53pti/ibLDkOEjO4+DRowd+wfSwkmWGVL3X320mvCfrg/aMN71B/cyyhW0mQ4cxqh2UcnpxLm0v/+uC7dSCyAJwIDAQAB";
    static {
       alipayClient= new DefaultAlipayClient(
               "https://openapi.alipay.com/gateway.do",
               APP_ID,
               APP_PRIVATE_KEY,
               "json"
               ,"UTF-8",
               ALIPAY_PUBLIC_KEY,
               "RSA2");
   }


   /**
    * 统一收单下单并支付页面接口
    * 参数：*/
    public static void pay(AliPayDto dto) throws AlipayApiException {
        //创建请求对象
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //设置请求信息 对应的参数 要求必选参数必须提供
        request.setBizContent(JSON.toJSONString(dto));
        //执行请求 并 获取响应
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        //验证是否成功
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }
    /**
     * 统一收单交易创建接口，主要通过二维码支付
     * */
    public static String preCreate(AliPayDto dto) throws AlipayApiException {
        //创建请对象
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        //设置请求参数
        request.setBizContent(JSON.toJSONString(dto));
        AlipayTradePrecreateResponse response=alipayClient.execute(request);
        if(response.isSuccess()){
            //返回预支付链接
            return response.getQrCode();
        }else {
            return response.getSubMsg();
        }
    }
    /**
     * 查询订单的支付状态*/
    public static String queryPayStatus(AliPayDto dto) throws AlipayApiException {
        AlipayTradeQueryRequest request=new AlipayTradeQueryRequest();
//        request.setBizContent("{\"out_trade_no\":\""+oid+"\"}");
        request.setBizContent(JSON.toJSONString(dto));
        AlipayTradeQueryResponse response=alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("订单支付查询成功");
            return response.getTradeStatus();
        }else {
            return response.getSubMsg();
        }
    }

    //统一交易收单撤销接口
    public static String cancelPay(AliPayDto dto) throws AlipayApiException{
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        request.setBizContent(JSON.toJSONString(dto));
        AlipayTradeCancelResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("收单撤销接口调用成功");
            return response.getAction();
        } else {
            System.out.println("收单撤销接口调用失败");
            return response.getSubMsg();
        }
    }

    //统一交易退款接口
    public static String returnPay(AliPayDto dto) throws AlipayApiException{
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(JSON.toJSONString(dto));
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("退款接口调用成功");
            return response.getPresentRefundBuyerAmount();
        } else {
            System.out.println("退款接口调用失败");
            return response.getSubMsg();
        }
    }

    /**
     * 统一收单交易退款查询
     * */
    public static String refundQuery(AliPayDto dto) throws AlipayApiException {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
//        request.setBizContent("{\"out_trade_no\":\""+oid+"\",\"out_request_no\":\""+oid+"\"}");
        request.setBizContent(JSON.toJSONString(dto));
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("退款查询调用成功");
            return response.getRefundAmount();
        } else {
            System.out.println("退款查询调用失败");
            return response.getSubMsg();
        }
    }

    /**
     * 统一收单交易关闭
     */
    public static String close(AliPayDto dto)  throws AlipayApiException{
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizContent(JSON.toJSONString(dto));
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
            return  response.getOutTradeNo();
        } else {
            System.out.println("调用失败");
            return response.getSubMsg();
        }
    }
}
