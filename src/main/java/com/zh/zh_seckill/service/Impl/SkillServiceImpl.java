package com.zh.zh_seckill.service.Impl;

import com.alibaba.fastjson.JSON;
import com.zh.zh_seckill.common.MsgUtil;
import com.zh.zh_seckill.config.RabbitmqConfig;
import com.zh.zh_seckill.config.RedisKeyConfig;
import com.zh.zh_seckill.dao.SkillDao;
import com.zh.zh_seckill.dto.SkillDto;
import com.zh.zh_seckill.entity.Order;
import com.zh.zh_seckill.entity.SkillGoods;
import com.zh.zh_seckill.service.SkillService;
import com.zh.zh_seckill.util.IdGenerator;
import com.zh.zh_seckill.vo.R;
import com.zh.zh_seckill.vo.SeckillGoodsVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class SkillServiceImpl implements SkillService {
    @Autowired
    private SkillDao skillDao;

    @Value("${skill.maxcount}")
    private int maxCount;
    private IdGenerator idGenerator=new IdGenerator();
    //做状态标记
    ConcurrentHashMap<String, Boolean> cMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap getMap(){
        return cMap;
    }
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //返回所有要参与秒杀的商品
    @Override
    public R select() {
        List<SeckillGoodsVo> list = skillDao.select();
        return R.ok(list);
    }

    //判断开始时间和库存
    @Override
    public R selectTime(SkillDto dto) {
        Integer id = dto.getId();
        Map<String, Object> map = new HashMap<>();
        SkillGoods goods = skillDao.selectOne(id);
        if (StringUtils.isEmpty(goods)){
            return R.error("该产品不存在");
        }
        Date stime = goods.getBeginTime();
        Date etime = goods.getEndTime();
        Date date = new Date();
        String url = null; //秒杀接口携带验证参数，间接实现接口动态隐藏
        long residue = 0;  //秒杀剩余时间
        int status = 0;    //秒杀状态 -1 未开始 0 进行中 1已结束
        //未开始
        if((stime.getTime() - date.getTime()) > 0){
            residue = (stime.getTime() - date.getTime()) / 1000;
            status = -1;
        }else if ((date.getTime() - etime.getTime()) < 0){
            //进行中，查看库存
            String stock = (String)redisTemplate.opsForHash().get(RedisKeyConfig.SKILL_GOODS, RedisKeyConfig.SKILL_GOODS_ID + id);
            int sStock = 0;
            if (!StringUtils.isEmpty(stock)){
                sStock = Integer.parseInt(stock);
                if (sStock > 0){
                    status = 0;
                    residue = 0;
                    url = MsgUtil.skillUrl + id;
                }
            }else {
                status = 1;
                residue = -1;
            }
        }else {
            status = 1;
            residue = -1;
        }
        map.put("status",status);
        map.put("residue",residue);
        map.put("url",url);
        return R.ok(map);
    }

    //同款产品一人只能最多买两件，同一账户只能参与一次同产品活动
    @Override
    public R kill(SkillDto dto) {
        Integer id = dto.getId();
        Integer num = dto.getNum();
        String url = dto.getUrl();
        Integer uid = dto.getUid();
        Double price = dto.getPrice();
        //判断url标记
        if (StringUtils.isEmpty(url) || !url.equals(MsgUtil.skillUrl + id)){
            return R.error("非法请求");
        }
        //判断购买数量是否超过限制
        if (num <= maxCount){
            if (cMap.containsKey(MsgUtil.SKILL_GOODS + id)){
                return R.error("shnagpinyishouwan商品已售完");
            }
            //判断商品库存剩余 采用redis库存预减  stock 会被杀穿，成为负数，但订单正常
            Long stock = redisTemplate.opsForHash().increment(RedisKeyConfig.SKILL_GOODS, RedisKeyConfig.SKILL_GOODS_ID + id, -num);
            if (stock >= 0){
                //判断是否是重复购买
                if (!redisTemplate.opsForHash().hasKey(RedisKeyConfig.SKILL_ORDER + id, RedisKeyConfig.SKILL_USER + uid)){
                    //生成订单
                    Order order1 = new Order();
                    order1.setOrderId(idGenerator.nextId()); //雪花算法生成id；
                    order1.setStatus(1);
                    order1.setNum(num);
                    order1.setGoodsId(id);
                    order1.setTotalPrice(price * num);
                    order1.setUid(uid);
                    order1.setType(2);
                    //下单，通过rabbitmq，异步生成订单
                    Map<String, Object> mqMap = new HashMap<>();
                    mqMap.put("order",order1);
                    //第一个参数交换机名称，第二，省略路由关键字，第三，消息
                    rabbitTemplate.convertAndSend(RabbitmqConfig.fanoutExchange, "", mqMap);
                    //添加改用户已经购买信息到redis，
                    redisTemplate.opsForHash().put(RedisKeyConfig.SKILL_ORDER + id, RedisKeyConfig.SKILL_USER + uid, JSON.toJSONString(order1));
                    //设置改商品的购买记录的有效期
                    redisTemplate.expire(RedisKeyConfig.SKILL_ORDER + id, MsgUtil.SKILL_TIMES, TimeUnit.MINUTES);
                    //用户购买结束
                    return R.ok(order1);
                }else {
                    return R.error("您已经购买过，不能重复购买");
                }
            }
            cMap.put(MsgUtil.SKILL_GOODS + id, true);
            return R.error("kuncunbuzu库存不足，活动结束");
        }else {
            return R.error("秒杀数量超过限制");
        }
    }

    @Override
    public void add(){
        redisTemplate.opsForHash().put(RedisKeyConfig.SKILL_GOODS, RedisKeyConfig.SKILL_GOODS_ID + 1, 300 + "");
    }
}
