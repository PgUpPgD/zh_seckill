package com.zh.zh_seckill.controller;

import io.goeasy.GoEasy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-28 16:10
 */
@RestController
public class GoEasyController {
    GoEasy goEasy = new GoEasy( "https://rest-hangzhou.goeasy.io", "BC-36808744fcda4503a8e8367e44297e9f");

    @GetMapping("/api/chat/msg")
    public String sendMsg(){
        DispatcherServlet s;
        goEasy.publish("my_channel", "Hello, GoEasy!");
        return "OK";
    }
}
