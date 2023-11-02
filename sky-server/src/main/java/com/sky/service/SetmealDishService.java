package com.sky.service;

import com.sky.vo.DishItemVO;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/2 13:44
 * @description: 套餐菜品关系
 */
public interface SetmealDishService {
    /**
     * 根据套餐id查询对应的菜品信息
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemVO(Long id);
}
