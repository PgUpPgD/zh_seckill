package com.zh.zh_seckill.config;

public class RedisKeyConfig {

    //记录登录的令牌  --校验令牌
    public static final String TOKEN_USER="skill:token:";//有效期1小时,后面跟令牌
    //记录登录的账号  --实现唯一登录
    public static final String USER_TOKEN="skill:user:";//有效期1小时,后面跟手机号

    public static final int TOKEN_HOURS=1;//有效期 令牌

    //记录被挤掉的令牌
    public static final String TOKEN_SWAP="skill:token:swap";//后面记录令牌 ，SET

    //记录错误的次数 5分钟内的错误次数
    public static final String USER_PASSFAIL="skill:userfail:";//String 手机号:时间戳 值 有效期5分钟

    //记录冻结的账号
    public static final String USER_FREEZE="skill:userfreeze:";//后面跟手机号 String 有效期 30分钟

    //冻结时间
    public static final int USER_FREEZE_TIME = 60;//有效期 60分钟

    //记录秒杀商品的库存 Hash 字段：秒杀商品id 值：库存量
    public static final String SKILL_GOODS="skill:goodskc";//记录秒杀的商品和对应的库存 有效期 永久删除

    //记录秒杀订单
    public static final String SKILL_ORDER="skill:order";//记录秒杀的订单 Hash

    //记录商品id
    public static final String SKILL_GOODS_ID="skill:goodsId:";

    //记录用户购买信息
    public static final String SKILL_USER="skill:user";

    //记录令牌桶的 令牌信息
    public static final String LIMIT_BUCKET="skill:limit";//采用List 存储令牌 永久有效
}