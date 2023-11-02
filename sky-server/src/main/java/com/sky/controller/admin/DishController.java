package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


/**
 * @Author wzy
 * @Date 2023/10/23 20:40
 * @description: 菜品管理
 */
@RestController
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    @ApiOperation("新增菜品")
    public Result add(@RequestBody DishDTO dishDTO){
        dishService.addWithFlavor(dishDTO);
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页数据：{}",dishPageQueryDTO);
       PageResult pageResult =  dishService.pageQuery(dishPageQueryDTO);
       return Result.success(pageResult);
    }
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}",ids);
        dishService.deleteBatch(ids);
        //清理redis中的缓存数据
        cleanCache("dish*");
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id){
        log.info("根据id查询菜品：{}",id);
        DishVO dishVO = dishService.getDishById(id);
        return Result.success(dishVO);
    }
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改的菜品：{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        //清理redis中的缓存数据
        cleanCache("dish*");
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("菜品的停售和起售")
    public Result updateStatus(@PathVariable Integer status,Long id){
        dishService.updateStatus(status,id);
        //清除redis中的缓存
        DishVO dish = dishService.getDishById(id);
        Long categoryId = dish.getCategoryId();//当前菜品的分类id
        String key = "dish"+categoryId;
        cleanCache(key);
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> listByCategoryId(Long categoryId){
        List<Dish> dishList = dishService.queryByCategoryId(categoryId);
        log.info("查询到的菜品是：{}",dishList);
        return Result.success(dishList);
    }

    /**
     * 统一清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
