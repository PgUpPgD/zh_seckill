package com.zh.zh_seckill.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zh.zh_seckill.config.RedisKeyConfig;
import com.zh.zh_seckill.entity.User;
import com.zh.zh_seckill.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//@Component
public class TokenFilter implements Filter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getRequestURI().indexOf("/sign") != -1
        || request.getRequestURI().indexOf("/login") != -1){
            filterChain.doFilter(request, response);
        }else {
            String token = request.getHeader("token");
            if (StringUtils.isEmpty(token)){
                returnJson(response);
                return;
            }
            String json = JwtUtil.parseToken(token);
            if(!StringUtils.isEmpty(json)){
                User user = JSON.parseObject(json, User.class);
                if (StringUtils.isEmpty(user)){
                    returnJson(response);
                    return;
                }
                String phone = user.getPhone();
                String tokens = redisTemplate.opsForValue().get(RedisKeyConfig.TOKEN_USER + phone);
                if(token.equals(tokens)){
                    filterChain.doFilter(request, response);
                    return;
                }else {
                    returnJson(response);
                    return;
                }
            }
            returnJson(response);
        }
    }

    private void returnJson(HttpServletResponse response){
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            JSONObject json = new JSONObject();
            json.put("code", 1);
            json.put("msg", "用户令牌token无效");
            writer.print(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                writer.close();
            }
        }
    }
}
