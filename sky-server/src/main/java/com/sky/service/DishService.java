package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * @Author wzy
 * @Date 2023/10/23 20:45
 * @description: 菜品业务接口
 */
public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     */
    void addWithFlavor(DishDTO dishDTO);
}
