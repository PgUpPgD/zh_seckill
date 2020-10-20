package com.zh.zh_seckill.dao;

import com.zh.zh_seckill.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao {
    Order findById(Long id);
    int save(Order order);
    int updateOrder(@Param("orderId")Long orderId, @Param("status")Integer status);
}
