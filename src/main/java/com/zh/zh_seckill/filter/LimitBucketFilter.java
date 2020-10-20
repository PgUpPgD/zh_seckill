package com.zh.zh_seckill.filter;

import com.alibaba.fastjson.JSON;
import com.zh.zh_seckill.config.RedisKeyConfig;
import com.zh.zh_seckill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 实现限流算法，令牌桶算法
 */
//@Component
public class LimitBucketFilter implements Filter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

//    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //令牌桶：按照一定的速度生成令牌，请求去令牌桶里（容量池）获取令牌
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        //只对秒杀接口进行 限流
        if(request.getRequestURI().endsWith("/api/skill/kill")){
            //进行限流
            //取到令牌放行  令牌一旦取出---就要删除
            Long i= Long.parseLong(redisTemplate.opsForList().leftPop(RedisKeyConfig.LIMIT_BUCKET));
            if(true){   //i!=null
                //取到  放行
                filterChain.doFilter(request,servletResponse);
            }else {
                //取不到  就拦截
                response.setContentType("application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println(JSON.toJSONString(R.ok("网络拥堵，暂且等待")));
            }
        }else {
            filterChain.doFilter(request,servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
