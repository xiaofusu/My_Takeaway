package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/2 11:28
 * @description: 用户端菜品查询
 */
@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "C端菜品接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> dishList(Long categoryId){
        //构造redis 中的key
        String key = "dish"+categoryId;
        //查询redis中是否存在缓存菜品数据
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //如果存在直接返回 无需查询数据库
        if(dishVOList!=null&&dishVOList.size()>0){
            return Result.success(dishVOList);
        }
        //不存在则查询数据库 并将其写入redis缓存中
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//起售的菜品
        dishVOList = dishService.listWithFlavor(dish);
        //放入redis
        redisTemplate.opsForValue().set(key,dishVOList);
        return Result.success(dishVOList);


    }

}
