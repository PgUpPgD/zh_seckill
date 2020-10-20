package com.zh.zh_seckill.service.Impl;

import com.alibaba.fastjson.JSON;
import com.zh.zh_seckill.config.RedisKeyConfig;
import com.zh.zh_seckill.dao.UserDao;
import com.zh.zh_seckill.dto.LoginTokenDto;
import com.zh.zh_seckill.dto.UserDto;
import com.zh.zh_seckill.entity.User;
import com.zh.zh_seckill.service.UserService;
import com.zh.zh_seckill.util.EncryptUtil;
import com.zh.zh_seckill.util.JwtUtil;
import com.zh.zh_seckill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    //模板对象
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @param dto
     * @return
     * 1.目前根据应用的体验，把注册和登录放在一起，存在就登录，不存在注册
     * 2.手机号 + 验证码 登录或注册，发送验证码，验证码和手机号存储数据库，在验证用户信息时，用手机号
     *    查取最新验证码，进行比对，成功或失败都要对当前验证码进行标记（已验证，一个验证码只能用一次）
     * 3.短信发送次数验证，防止盗刷，设置一分钟，一小时，一天的发送次数，通过redis实现
     * 4.用户名密码登录失败超过限定次数进行账户锁定(如24小时内连续失败5次，成功则清掉redis失败次数记录)
     * 5.唯一登录，实现token的刷新，并提示用户已被挤下线。
     * 6.异地登录时，提醒用户账户异地的登录，登录地点在登录时获取用户ip地址，通过GeoLite2-City解析
     * 7.配置超级账号，直接登录。配置超级验证码，所有通用。
     */
    @Override
    public R sign(UserDto dto) {
        String phone = dto.getPhone();
        User users = userDao.login(phone);
        if (!StringUtils.isEmpty(users)){
            return R.error("当前账户已注册");
        }
        String pwd = dto.getPwd();
        String str = EncryptUtil.md5(pwd);
        User user = new User();
        user.setPassword(str);
        user.setPhone(phone);
        int i = userDao.sign(user);
        if (i > 0){
            return R.ok();
        }
        return R.error();
    }

    @Override
    public R login(UserDto dto) {
        //判断账户是否已经被冻结
        String phone = dto.getPhone();
        if (redisTemplate.hasKey(RedisKeyConfig.USER_FREEZE + phone)){
            //账号被冻结   返回剩余过期时间，单位秒
            Long expire = redisTemplate.getExpire(RedisKeyConfig.USER_FREEZE + phone);
            return R.ok("账号已冻结，距解冻时间还有" + expire + "秒");
        }
        //判断验证码登录还是密码登录
        if (dto.getType() == 1){
            User user = userDao.login(phone);
            String password = user.getPassword();
            //加密比对密码
            if (EncryptUtil.md5(dto.getPwd()).equals(password)){
                //成功，校验账户是否登录过，唯一登录，删除上次的登录token
                if (redisTemplate.hasKey(RedisKeyConfig.TOKEN_USER + phone)){
                    //登录过,获取该账户上次的登录令牌
                    String str = redisTemplate.opsForValue().get(RedisKeyConfig.TOKEN_USER + phone);
                    //记录到挤掉信息中， 过滤器中验证token是否存在挤掉信息中
                    redisTemplate.opsForSet().add(RedisKeyConfig.TOKEN_SWAP, str);
                    //删除原来的令牌
                    redisTemplate.delete(RedisKeyConfig.TOKEN_USER + str);
                }
                //准备返回用户的相关信息和令牌
                LoginTokenDto tokenDto = new LoginTokenDto();
                tokenDto.setUser(user);   //返回前端需要的内容
                String json = JSON.toJSONString(user);
                String token = JwtUtil.createToken(json);
                tokenDto.setToken(token);
                //加验签
//                try {
//                    tokenDto.setVerify(MD5Util.UrlEncode(token + phone));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                //存redis
                //记录用户对应的令牌, 同一个用户的 key 是相同的，第二次登录时，自动覆盖，token值改变
                redisTemplate.opsForValue().set(RedisKeyConfig.TOKEN_USER + phone, token,
                        RedisKeyConfig.TOKEN_HOURS, TimeUnit.HOURS);
                //记录令牌对应的用户信息
                redisTemplate.opsForValue().set(RedisKeyConfig.TOKEN_USER + token, json,
                        RedisKeyConfig.TOKEN_HOURS, TimeUnit.HOURS);
                //返回用户的相关信息和令牌
                return R.ok(tokenDto);
            }else {
                //60分钟内密码错误5次及以上，冻结账户60分钟。
                int count = 0;
                //校验是否达到最大的失败次数
                Set set = redisTemplate.keys(RedisKeyConfig.USER_PASSFAIL + phone + ":*");
                if (set != null && set.size() > 1){
                    count = set.size();
                    if (count >= 4){
                        //已经错了4次加这一次5次，冻结，存冻结账户，失效时间60分钟,value存冻结账户时的时间
                        redisTemplate.opsForValue().set(RedisKeyConfig.USER_FREEZE + phone,
                                System.currentTimeMillis() / 1000 + "", RedisKeyConfig.USER_FREEZE_TIME, TimeUnit.MINUTES);
                    }
                }
                //密码校验失败，添加失败次数的统计
                redisTemplate.opsForValue().set(RedisKeyConfig.USER_PASSFAIL + phone + ":" +
                        System.currentTimeMillis(), "", RedisKeyConfig.USER_FREEZE_TIME, TimeUnit.MINUTES);
                count += 1;
                return R.error("你已经失败了" + count + "次，小心账号被冻结哟！");
            }
        }else {
            //验证码登录
            return null;
        }
    }
}
