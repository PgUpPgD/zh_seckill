package com.zh.zh_seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.zh.zh_seckill.dao")
@EnableScheduling
@SpringBootApplication
public class ZhSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhSeckillApplication.class, args);
    }

}
