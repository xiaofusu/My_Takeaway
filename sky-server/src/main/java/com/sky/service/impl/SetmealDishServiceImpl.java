package com.sky.service.impl;

import com.sky.mapper.SetmealDishMapper;
import com.sky.service.SetmealDishService;
import com.sky.vo.DishItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/2 13:43
 * @description: 套餐菜品关系
 */
@Service
public class SetmealDishServiceImpl implements SetmealDishService {
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 根据套餐id查询对应的里面菜品信息
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemVO(Long id) {
        List<DishItemVO> dishItemVOList = setmealDishMapper.getDishItemVO(id);
        return dishItemVOList;
    }
}
