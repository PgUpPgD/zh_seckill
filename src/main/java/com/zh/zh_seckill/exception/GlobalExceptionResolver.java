package com.zh.zh_seckill.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

//@RestControllerAdvice
public class GlobalExceptionResolver {
    //捕获所有异常 获取内部异常处理类：Exception
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionResolver.class);

    @ResponseBody
    @ExceptionHandler(value = MyException.class)
    public Map myException(MyException e, HttpServletRequest request){

        String eMessage = e.getMsg();
        Integer code = e.getCode();
        StringBuffer url = request.getRequestURL();
        System.out.println("ErrorMsg-->"+eMessage);
        System.out.println("url-->"+url);
        //打印日志
        LOG.error("错误信息-->"+eMessage+"===请求链接--->"+url+"错误状态码--->"+code);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("msg",eMessage);
        map.put("code",code);
        map.put("url",url);
        return map;
    }

    @ExceptionHandler(value = Exception.class) //全局异常
    public Map exception(Exception e, HttpServletRequest request){

        String eMessage = e.getMessage();
        StringBuffer url = request.getRequestURL();
        System.out.println("ErrorMsg -->" + eMessage);
        System.out.println("url -->" + url);
        //打印日志
        LOG.error("错误信息 -->" + eMessage + "===请求链接--->" + url);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("msg",eMessage);
        map.put("url",url);
        return map;
    }

}