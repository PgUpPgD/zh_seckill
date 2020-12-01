package com.zh.zh_seckill.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Data
@Component
public class RedissonConfig {

    @Value("${custom.redis.addr}")
    private String addr;

    @Value("${custom.redis.pwd}")
    private String pwd;

    @Value("${custom.redis.perms-db}")
    private int permsDB;

    private static RedissonClient redisson;

    private static RedissonConfig instance = null;
    private RedissonConfig(){}
    public static RedissonConfig getInstance(){
        if (instance == null){
            instance = new RedissonConfig();
        }
        return instance;
    }

    @PostConstruct
    public void init(){
        Config config = new Config();
        config.setCodec(new StringCodec());
        config.useSingleServer()
                .setAddress(addr)
                .setPassword(pwd)
                .setDatabase(permsDB);

        redisson = Redisson.create(config);
        instance = this;
    }

    public static void setStr(String key, String msg, long seconds) {
        if (seconds > 0) {
            redisson.getBucket(key).set(msg, seconds, TimeUnit.SECONDS);
        } else {
            redisson.getBucket(key).set(msg);
        }
    }

    public static void main(String[] args) {
        RedissonConfig manager = new RedissonConfig();
        manager.setAddr("redis://121.40.19.153:6379");
        manager.setPwd("thinkive_123456");
        manager.setPermsDB(0);
        manager.init();

        RedissonConfig.setStr("hello_redis", "helo123", 100);
    }
}
