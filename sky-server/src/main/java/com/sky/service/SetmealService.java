package com.sky.service;

import com.sky.dto.SetmealDTO;

/**
 * @Author wzy
 * @Date 2023/10/29 15:59
 * @description: 套餐service接口
 */
public interface SetmealService {
    /**
     * 新增套餐
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);
}
