package com.zh.zh_seckill.config;

import com.zh.zh_seckill.filter.LimitBucketFilter;
import com.zh.zh_seckill.filter.TokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

//@Configuration
public class FilterConfig {
//    @Order(1)
//    @Bean
    public FilterRegistrationBean addLimitBucket(LimitBucketFilter filter){
        FilterRegistrationBean bean=new FilterRegistrationBean();
        bean.addUrlPatterns("/*");
        bean.setFilter(filter);
        return bean;
    }
//    @Order(2)
//    @Bean
    public FilterRegistrationBean addToken(TokenFilter filter){
        FilterRegistrationBean bean=new FilterRegistrationBean();
        bean.addUrlPatterns("/*");
        bean.setFilter(filter);
        return bean;
    }
}