package com.zh.zh_seckill.listener;

import com.zh.zh_seckill.dao.OrderDao;
import com.zh.zh_seckill.dao.SkillDao;
import com.zh.zh_seckill.entity.Order;
import com.zh.zh_seckill.entity.SkillGoods;
import com.zh.zh_seckill.exception.MyException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
//监听简单队列
@RabbitListener(queues = "skill.queue.order")
public class OrderListener {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private SkillDao skillDao;

    @RabbitHandler
    public void findStatus(Map<String,Object> mqMap){
        Order order = (Order)mqMap.get("order");
        if (!StringUtils.isEmpty(order)){
            Integer num = order.getNum();
            Integer goodsId = order.getGoodsId();
            SkillGoods goods = skillDao.selectOne(goodsId);
            Double price = goods.getSeckillPrice();
            order.setTotalPrice(num * price);
            //持久化订单到数据库
            int i = orderDao.save(order);
            if (i <= 0){
                throw new MyException(1, "订单添加失败");
            }
        }
    }
}
