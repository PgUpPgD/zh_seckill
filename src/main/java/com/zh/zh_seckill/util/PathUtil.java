package com.zh.zh_seckill.util;

import java.util.Base64;
import java.util.Random;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-03-06 16:20
 */
public class PathUtil {
    //小的加密算法
    /**
     * 失效秒杀接口路径的变量生成*/
    public static String createPathPass(int sgid){
        //秒杀路径变量：秒杀商品id+当前的时间 毫秒
        String s=System.currentTimeMillis()+""+sgid;
        Random r=new Random();
        String pass= EncryptUtil.sha(EncryptUtil.SHA1,s);
        String p= Base64.getUrlEncoder().encodeToString(pass.substring(0,(r.nextInt(5)+3)).getBytes());
        return p.replaceAll("=","");
    }
}