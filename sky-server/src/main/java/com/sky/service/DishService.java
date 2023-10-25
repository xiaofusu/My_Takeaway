package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
}
