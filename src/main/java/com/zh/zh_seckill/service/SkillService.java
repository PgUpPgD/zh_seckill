package com.zh.zh_seckill.service;

import com.zh.zh_seckill.dto.SkillDto;
import com.zh.zh_seckill.vo.R;

public interface SkillService {
    R select();
    R selectTime(SkillDto dto);
    R kill(SkillDto dto);
    void add();
}
