package com.zh.zh_seckill.task;

import com.zh.zh_seckill.config.RedisKeyConfig;
import com.zh.zh_seckill.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

//@Component
public class LimitTokenTask {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private IdGenerator idGenerator=new IdGenerator();
    //默认桶的容量
    private int maxCount=1000;
    //生成令牌的频率 秒级
    private int secondCount=100;//每秒生成100个
    //cron表达式 设置定时时间
//    @Scheduled(cron = "* * * * * ?")
    public void createLT(){
        for(int i=1;i<=secondCount;i++) {
            //验证是否达到上限
            if (redisTemplate.opsForList().size(RedisKeyConfig.LIMIT_BUCKET) < maxCount) {
                redisTemplate.opsForList().leftPush(RedisKeyConfig.LIMIT_BUCKET,idGenerator.nextId()+"");
            }
        }
    }
}
