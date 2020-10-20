package com.zh.zh_seckill.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.file.IOUtils;
import com.zh.zh_seckill.dto.AliPayDto;
import com.zh.zh_seckill.dto.WxPayDto;
import com.zh.zh_seckill.exception.MyException;
import com.zh.zh_seckill.pay.AliPayUtil;
import com.zh.zh_seckill.pay.WxchatPayUtil;
import com.zh.zh_seckill.util.QrcodeUtil;
import com.zh.zh_seckill.vo.R;
import io.goeasy.GoEasy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Api(value = "支付宝和微信支付")
@Controller
public class PayController {
    private GoEasy goEasy = new GoEasy( "https://rest-hangzhou.goeasy.io", "BC-36808744fcda4503a8e8367e44297e9f");

    @ApiOperation(value = "生成二维码，让用户付款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "out_trade_no", value = "订单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "total_amount", value = "订单金额", required = true, dataType = "Double", paramType = "path"),
            @ApiImplicitParam(name = "subject", value = "订单标题", required = true, dataType = "String", paramType = "path")
    })
    @PostMapping("api/pay/aliPrecreate.do")
    public void pay(AliPayDto dto, HttpServletResponse response) throws AlipayApiException, IOException {
        //调用支付方法 获取预支付链接
        String no = dto.getOut_trade_no();
        String s = dto.getSubject();
        Double a = dto.getTotal_amount();
        if (StringUtils.isEmpty(no) || StringUtils.isEmpty(s) || StringUtils.isEmpty(a)){
            throw new MyException(1,"必要参数不能为空");
        }
        String u= AliPayUtil.preCreate(dto);
        if(u!=null){
            //生成缓存 二维码图片
            BufferedImage bufferedImage= QrcodeUtil.createQrcode(u,400);
            //将图片写出去
            ImageIO.write(bufferedImage,"jpeg",response.getOutputStream());
        }
    }

    @ApiOperation(value = "查询支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "out_trade_no", value = "订单号", required = true, dataType = "String", paramType = "path"),
    })
    @PostMapping("api/pay/aliQuery.do")
    @ResponseBody
    public R query(@RequestBody AliPayDto dto) throws AlipayApiException {
        String out_trade_no = dto.getOut_trade_no();
        if (StringUtils.isEmpty(out_trade_no)){
            return R.error("订单号不能为空");
        }
        return R.ok(AliPayUtil.queryPayStatus(dto));
    }

    @ApiOperation(value = "支付交易返回失败或支付系统超时，调用该接口撤销交易")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "out_trade_no", value = "订单号", required = true, dataType = "String", paramType = "path")
    })
    @PostMapping("api/pay/aliCancel.do")
    @ResponseBody
    public R cancenl(@RequestBody AliPayDto dto) throws AlipayApiException {
        return R.ok(AliPayUtil.cancelPay(dto));
    }

    @ApiOperation(value = "退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "out_trade_no", value = "订单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "refund_amount", value = "退款金额", required = true, dataType = "Double", paramType = "path")
    })
    @PostMapping("api/pay/aliRefund.do")
    @ResponseBody
    public R refund(@RequestBody AliPayDto dto) throws AlipayApiException {
        return R.ok(AliPayUtil.returnPay(dto));
    }

    @ApiOperation(value = "查询退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "out_trade_no", value = "订单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "out_request_no", value = "退款请求号", required = true, dataType = "String", paramType = "path")
    })
    @PostMapping("api/pay/refundQuery.do")
    @ResponseBody
    public R refundQuery(@RequestBody AliPayDto dto) throws AlipayApiException {
        return R.ok(AliPayUtil.refundQuery(dto));
    }

    @ApiOperation(value = "当用户长时间未付款，可关闭当前交易")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "out_trade_no", value = "订单号", required = true, dataType = "String", paramType = "path"),
    })
    @PostMapping("api/pay/close.do")
    @ResponseBody
    public R close(@RequestBody AliPayDto dto) throws AlipayApiException {
        return R.ok(AliPayUtil.close(dto));
    }

    //微信支付的接口
    //创建支付订单
    @PostMapping("api/pay/wxpayprepay.do")
    public void createWxPay(WxPayDto payDto, HttpServletResponse response) throws IOException {
        payDto.setTotal_fee(1);
        String u= WxchatPayUtil.createPay(payDto);
        if(u!=null){
            //生成缓存 二维码图片
            BufferedImage bufferedImage= QrcodeUtil.createQrcode(u,400);
            //将图片写出去
            ImageIO.write(bufferedImage,"jpeg",response.getOutputStream());
        }
    }
    //查询支付状态
    @GetMapping("api/pay/wxpayquery.do")
    @ResponseBody
    public R queryPay(String oid) throws AlipayApiException {
        return R.ok(WxchatPayUtil.queryPay(oid));
    }
    //取消支付订单 (未付款)
    @GetMapping("api/pay/wxpaycancel.do")
    @ResponseBody
    public R cancel(String oid) throws AlipayApiException {
        return R.ok(WxchatPayUtil.closePay(oid));
    }
    //接收微信支付的异步通知 如果说微信支付成功，微信方 会请求我们的接口
    @GetMapping("api/pay/wxchatnotify.do")
    public void notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ArrayList list;
        HashMap map1;
        SqlSessionFactoryBean bean;
        //finalize();
        // SoftReference;
        ConcurrentHashMap map2;
        //1.接收消息
        String xml=new String(IOUtils.toByteArray(request.getInputStream()));
        HashMap<String,Object> map=WxchatPayUtil.parseXml(xml);
        //2.更改数据库对应得到订单的支付状态
        if(map.get("result_code").equals("SUCCESS")){
            String oid=map.get("out_trade_no").toString();
            int money=Integer.parseInt(map.get("total_fee").toString());
            //根据查询订单 校验金额是否一致
            //一致 更改订单状态--->未支付--->已支付,代发货
        }

        //省略……

        //3.通知前端 支付结果
        goEasy.publish("channel_orderpay",map.get("result_code").toString());
        //4.返回消息 固定
        response.getWriter().write("<xml>"+
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>");
    }
}