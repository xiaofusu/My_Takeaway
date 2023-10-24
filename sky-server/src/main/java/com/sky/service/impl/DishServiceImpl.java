package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/10/23 20:45
 * @description: 菜品业务实现类
 */
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional
    @Override
    public void addWithFlavor(DishDTO dishDTO) {
        //插入菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        //获得dishMapper.insert(dish)生成的主键值 便于插入给dish_flavor口味表
        Long dishId = dish.getId();
        //插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&& flavors.size()>0){
            flavors.forEach((item)->{
                item.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
