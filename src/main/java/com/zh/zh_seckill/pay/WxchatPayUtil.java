package com.zh.zh_seckill.pay;

import com.zh.zh_seckill.dto.WxPayDto;
import com.zh.zh_seckill.util.HttpUtil;
import com.zh.zh_seckill.util.MD5Util;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 微信支付
 */
public class WxchatPayUtil {
    //秘钥信息  公司提供
    private static final String APPID="wx632c8f211f8122c6";  //
    private static final String MCHID="1497984412";
    private static final String APPKEY="sbNCm1JnevqI36LrEaxFwcaT0hkGxFnC";
    //回调接口  注意地址：必须是公网可以访问的
    private static final String CALL_URL="http://localhost:8901/api/pay/wxchatnotify.do";
    //微信支付常用接口
    //统一下单
    private static final String prepay="https://api.mch.weixin.qq.com/pay/unifiedorder";
    //查询订单支付接口
    private static final String querypay="https://api.mch.weixin.qq.com/pay/orderquery";
    //关闭订单接口
    private static final String closepay="https://api.mch.weixin.qq.com/pay/closeorder";
//    private static RestTemplate restTemplate=new RestTemplate();
    /**
     * 生成签名*/
    private static String createSign(TreeMap<String,Object> map){
        //1.设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数按照参数名ASCII码从小到大排序（字典序），
        // 使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA
            StringBuffer buffer=new StringBuffer();
            for(String k:map.keySet()){
                Object o=map.get(k);
                if(o!=null){
                    buffer.append(k+"="+o+"&");
                }
            }
            if(buffer.length()>0){
                buffer.delete(buffer.length()-1,buffer.length());
            }
            //2.，在stringA最后拼接上key得到stringSignTemp字符串，
        // 并对stringSignTemp进行MD5运算，
        // 再将得到的字符串所有字符转换为大写，
        // 得到sign值signValue。
            buffer.append("&key="+APPKEY);
            return MD5Util.MD5Encode(buffer.toString(),"UTF-8").toUpperCase();
    }
    //生成随机字符串
    public static String createNonceStr(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //Map-生成请求的xml
    public static String createXML(TreeMap<String,Object> map) throws IOException {
        //创建根节点
        Element root=new Element("xml");
        //创建文档对象
        Document document=new Document(root);
        //循环创建子节点 并添加
        for(String key:map.keySet()){
            Element child=new Element(key);
            child.setText(map.get(key).toString());
            document.getRootElement().addContent(child);
        }
        //创建xml输出器
        XMLOutputter xmlOutputter=new XMLOutputter();
        //创建输出内存流
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        //把内容写出到内存流
        xmlOutputter.output(document,baos);
        return baos.toString();
    }

    //解析xml格式---Map
    public static HashMap<String,Object> parseXml(String xml) throws Exception {
        HashMap<String,Object>  map=new HashMap<>();
        //转换数据
        ByteArrayInputStream bais=new ByteArrayInputStream(xml.getBytes());
        //创建解析器
        SAXBuilder builder=new SAXBuilder();
        //创建文档对象
        Document document=builder.build(bais);
        //获取根节点
        Element root=document.getRootElement();
        //遍历子节点
        List<Element> childs=root.getChildren();
        for(Element c:childs){
            map.put(c.getName(),c.getValue());
        }
        return map;
    }

    private static TreeMap<String,Object> createParam(){
        TreeMap<String,Object> map=new TreeMap<>();
        map.put("appid",APPID);
        map.put("mch_id",MCHID);
        map.put("nonce_str",createNonceStr());
        return map;
    }
    //统一下单
    public static String createPay(WxPayDto dto){
        TreeMap<String,Object> map=createParam();
        map.put("trade_type","NATIVE");
        map.put("notify_url",CALL_URL);
        map.put("body",dto.getBody());
        map.put("out_trade_no",dto.getOut_trade_no());
        map.put("total_fee",dto.getTotal_fee());
        map.put("spbill_create_ip","127.0.0.1");
        map.put("sign",createSign(map));
        try {
            //生成请求的xml
            String requestXml=createXML(map);
            String responseXml= HttpUtil.postData(prepay,requestXml);
            if(responseXml!=null){
                HashMap<String,Object> resMap=parseXml(responseXml);
                if(resMap.containsKey("code_url")){
                    return resMap.get("code_url").toString();
                }else {
                    System.out.println(resMap.get("return_msg").toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 查询微信支付的 支付状态*/
    public static String queryPay(String oid){
        TreeMap<String,Object> map=createParam();
        map.put("out_trade_no",oid);
        map.put("sign",createSign(map));
        try {
            //生成请求的xml
            String requestXml=createXML(map);
            String responseXml=HttpUtil.postData(querypay,requestXml);
            if(responseXml!=null){
                HashMap<String,Object> resMap=parseXml(responseXml);
                if(resMap.containsKey("trade_state")){
                    return resMap.get("trade_state").toString();
                }else {
                    System.out.println(resMap.get("return_msg").toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 取消微信支付关闭订单*/
    public static String closePay(String oid){
        TreeMap<String,Object> map=createParam();
        map.put("out_trade_no",oid);
        map.put("sign",createSign(map));
        try {
            //生成请求的xml
            String requestXml=createXML(map);
            String responseXml=HttpUtil.postData(closepay,requestXml);
            if(responseXml!=null){
                HashMap<String,Object> resMap=parseXml(responseXml);
                if(resMap.containsKey("result_code")){
                    return resMap.get("result_code").toString();
                }else {
                    System.out.println(resMap.get("return_msg").toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}