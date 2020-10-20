package com.zh.zh_seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqConfig {
    //1.定义变量
    //定义队列名称
    public String simpleName = "skill.queue.order"; //简单队列，同步Mysql订单数据
    public String qDefer = "skill.queue.defer"; //延迟队列，设置消息的有效期，秒杀时长
    public String qDeath = "skill.queue.death"; //死信队列，进行校验订单是否完成支付

    //定义交换器名称
    public static final String fanoutExchange = "skill.exchange.order"; //fanout直接转发，用于订单数据直接发送
    public String directExchange = "skill.exchange.death"; //direct 路由匹配 用户订单数据发送

    //定义路由规则
    public String routingKey = "skillOrderDeath"; //死信队列路由匹配

    //2.创建两个交换器三个队列
    //简单队列
    @Bean
    public Queue simpleQueue(){
        return new Queue(simpleName);
    }
    //延迟队列，超时之后自动把消息发送到死信队列
    @Bean
    public Queue deferQueue(){
        //设置延迟队列参数
        Map<String, Object> map = new HashMap<>();
        //1.时间   毫秒
        map.put("x-message-ttl", 5 * 60 * 1000);
        //2.交换器
        map.put("x-dead-letter-exchange", directExchange);
        //3.路由关键字
        map.put("x-dead-letter-routing-key", routingKey);
        return QueueBuilder.durable(qDefer).withArguments(map).build();
    }
    //死信队列
    @Bean
    public Queue deathQueue(){
        return new Queue(qDeath);
    }

    //创建fanout交换机
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(fanoutExchange);
    }

    //创建direct交换机
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(directExchange);
    }

    //3.绑定队列和交换器
    @Bean
    public Binding createB1(){
        return BindingBuilder.bind(simpleQueue()).to(fanoutExchange());
    }
    @Bean
    public Binding createB2(){
        return BindingBuilder.bind(deferQueue()).to(fanoutExchange());
    }
    @Bean
    public Binding createB3(){
        return BindingBuilder.bind(deathQueue()).to(directExchange()).with(routingKey);
    }



}
