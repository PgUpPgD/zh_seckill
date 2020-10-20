package com.zh.zh_seckill.chat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/api/chat/{nickname}")
@Scope(scopeName = "prototype")  //设置IOC的创建Bean的类型，默认单例，改为多例
public class WebSocketServer {
    public WebSocketServer(){
        System.out.println("服务端构造");
    }
    //实例化集合，存储目前在线的聊天对象信息
    public static ConcurrentHashMap<String, WebSocketServer> map = new ConcurrentHashMap<>();
    //记录当前会话对象
    private Session session;
    //昵称
    private String nickname;

    //接收  连接消息（该方法触发说明前端有人连接）
    @OnOpen
    public void open(Session session, @PathParam("nickname") String nickname) throws IOException {
        if (map.containsKey(nickname)){
            session.getBasicRemote().sendText("您好，昵称已存在");
            session.close();
        }else {
            System.out.println("连接：" + nickname);
            this.nickname = nickname;
            this.session = session;
            map.put(nickname, this);
        }
    }

    //接收  传输消息
    @OnMessage
    public void message(String msg, Session session) throws IOException{
        System.out.println("接收消息：" + msg);
        batchMsg(msg);
    }

    //错误信息
    @OnError
    public void error(Session session, Throwable throwable){
        System.out.println("崩了"+throwable.getMessage());
    }

    //接收 关闭连接
    @OnClose
    public void close(Session session)throws IOException{
        System.out.println("关闭");
        map.remove(nickname);
        batchMsg(nickname + "下线了");
    }

    //群发消息
    private void batchMsg(String msg) throws IOException{
        for (String k:map.keySet()){
            if (!k.equals(nickname)){
                map.get(k).session.getBasicRemote().sendText(nickname+"-说："+msg);
            }
        }
    }

    //单发消息
    public void batchMsg(String msg, String name) throws IOException{
        for (String k:map.keySet()){
            if (k.equals(name)){
                map.get(k).session.getBasicRemote().sendText(name+"-说："+msg);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        WebSocketServer server = new WebSocketServer();
        server.batchMsg("大家好");
    }



}
