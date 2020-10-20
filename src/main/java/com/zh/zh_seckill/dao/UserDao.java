package com.zh.zh_seckill.dao;

import com.zh.zh_seckill.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    int sign(User user);
    User login(String phone);
}
