package com.zh.zh_seckill.dao;

import com.zh.zh_seckill.entity.SkillGoods;
import com.zh.zh_seckill.vo.SeckillGoodsVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillDao {
    List<SeckillGoodsVo> select();
    SkillGoods selectOne(Integer id);
}
