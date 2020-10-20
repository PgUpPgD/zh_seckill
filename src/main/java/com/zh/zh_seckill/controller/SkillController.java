package com.zh.zh_seckill.controller;

import com.zh.zh_seckill.dto.SkillDto;
import com.zh.zh_seckill.service.SkillService;
import com.zh.zh_seckill.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkillController {
    @Autowired
    private SkillService skillService;

    @PostMapping("api/skill/select")
    public R selectAllGoods(){
        return skillService.select();
    }

    @PostMapping("api/skill/selectTime")
    public R selectTime(@RequestBody SkillDto dto){
        return skillService.selectTime(dto);
    }

    @RequestMapping("api/skill/kill")
    public R kill(@RequestBody SkillDto dto){
        if (StringUtils.isEmpty(dto)){
            return R.error("参数不能为空");
        }
        return skillService.kill(dto);
    }

    @PostMapping("api/skill/add")
    public void add(){
        skillService.add();
    }


}
