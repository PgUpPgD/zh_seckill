package com.zh.zh_seckill.service;

import com.zh.zh_seckill.dto.UserDto;
import com.zh.zh_seckill.vo.R;

public interface UserService {
    R sign(UserDto dto);
    R login(UserDto dto);

}
