package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/2 12:44
 * @description: 用户端套餐相关
 */
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "C端套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/list")
    @ApiOperation("根据分类id套餐查询")
    public Result<List<Setmeal>> list(Long categoryId){
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);//起售中的套餐
        List<Setmeal> setmealList = setmealService.listQuery(setmeal);
        return Result.success(setmealList);
    }
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> dishList(@PathVariable Long id){
        List<DishItemVO> dishItemVOList = setmealDishService.getDishItemVO(id);
        return Result.success(dishItemVOList);
    }
}
