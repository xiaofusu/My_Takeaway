package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.springframework.stereotype.Service;

/**
 * @Author wzy
 * @Date 2023/11/1 13:29
 * @description: 微信用户
 */
public interface UserService {
    /**
     * 微信用户登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
