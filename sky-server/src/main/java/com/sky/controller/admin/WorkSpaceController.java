package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Author wzy
 * @Date 2023/11/10 8:13
 * @description: 管理端工作台相关接口
 */
@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "工作台相关接口")
@Slf4j
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;

    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> getOverviewOrders(){
        OrderOverViewVO orderOverViewVO =  workSpaceService.getOverviewOrders();
        return Result.success(orderOverViewVO);
    }

    @GetMapping("/businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO> getBusinessData(){
        //获得当天的开始时间
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        //获得当天的结束时间
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        BusinessDataVO businessDataVO =  workSpaceService.getBusinessData(begin,end);
        return Result.success(businessDataVO);
    }
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> getOverviewDishes(){
        DishOverViewVO dishOverViewVO =  workSpaceService.getOverviewDishes();
        return Result.success(dishOverViewVO);
    }

    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询菜品总览")
    public Result<SetmealOverViewVO> getOverviewSetmeals(){
        SetmealOverViewVO setmealOverViewVO =  workSpaceService.getOverviewSetmeals();
        return Result.success(setmealOverViewVO);
    }
}
