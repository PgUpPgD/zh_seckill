package com.zh.zh_seckill.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WebController {

    @Autowired
    private WebSocketServer webSocketServer;

    @RequestMapping("web/send")
    public String hello(){
        try {
            webSocketServer.batchMsg("大家好", "小明");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "调用成功";
    }
}
