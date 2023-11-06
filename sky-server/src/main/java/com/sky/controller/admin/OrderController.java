package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wzy
 * @Date 2023/11/6 19:54
 * @description: 管理端订单管理
 */
@RestController
@RequestMapping("/admin/order")
@Api(tags = "客户端订单相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/conditionSearch")
    public Result<PageResult> pageConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.pageConditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
}
