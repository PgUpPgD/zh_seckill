package com.zh.zh_seckill.listener;

import com.alibaba.fastjson.JSON;
import com.zh.zh_seckill.common.MsgUtil;
import com.zh.zh_seckill.config.RedisKeyConfig;
import com.zh.zh_seckill.dao.OrderDao;
import com.zh.zh_seckill.entity.Order;
import com.zh.zh_seckill.exception.MyException;
import com.zh.zh_seckill.service.Impl.SkillServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
//监听死信队列
@RabbitListener(queues = "skill.queue.death")
public class DeathListener {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private SkillServiceImpl service;
    @Autowired
    private StringRedisTemplate redisTemplate;

    //验证状态判断是否付款
    @RabbitHandler
    public void saveOrder(Map<String,Object> mqMap){
        System.out.println("死信队列开始运行");
        Order order = (Order)mqMap.get("order");
        if (!StringUtils.isEmpty(order)){
            Integer id = order.getGoodsId();
            Long oid = order.getOrderId();
            Integer uid = order.getUid();
            Integer num = order.getNum();
            //数据库查找该订单支付状态
            Order order1 = orderDao.findById(oid);
            if (!StringUtils.isEmpty(order1)){
                Integer status = order1.getStatus();
                String json = (String)redisTemplate.opsForHash().get(RedisKeyConfig.SKILL_ORDER + id, RedisKeyConfig.SKILL_USER +uid);
                Order order2 = JSON.parseObject(json, Order.class);
                //redis中订单状态
                Integer status1 = order2.getStatus();
                if (status == status1){
                    //30分钟状态未改变，未支付，标记订单超时，释放库存
                    int i = orderDao.updateOrder(oid, 7);
                    if (i <= 0){
                        throw new MyException(1,"id为：" + oid + "，超时订单状态修改失败");
                    }
                    String c = (String)redisTemplate.opsForHash().get(RedisKeyConfig.SKILL_GOODS, RedisKeyConfig.SKILL_GOODS_ID + id);
                    System.out.println("开始释放库存，当前值为：" + c);
                    int c1 = 0;
                    if (!StringUtils.isEmpty(c)){
                        c1 = Integer.parseInt(c);
                    }
                    redisTemplate.opsForHash().put(RedisKeyConfig.SKILL_GOODS, RedisKeyConfig.SKILL_GOODS_ID + id, c1 + num + "");
                    //问题; redis中库存已经为负数
                    String c2 = (String)redisTemplate.opsForHash().get(RedisKeyConfig.SKILL_GOODS, RedisKeyConfig.SKILL_GOODS_ID + id);
                    System.out.println("库存释放完毕，当前值为：" + c2);
                    //更改预减库存的内存标记
                    if(service.getMap().containsKey(MsgUtil.SKILL_GOODS + id)){
                        //之前的内存标记 商品售罄 恢复
                        service.getMap().remove(MsgUtil.SKILL_GOODS + id);
                    }
                    System.out.println("死信队列执行完毕");
                }
            }
            System.out.println("数据库丢失订单：" + order);
        }
    }

}
