package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @Author wzy
 * @Date 2023/11/5 12:39
 * @description: 用户订单
 */
public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO OrderSubmit(OrdersSubmitDTO ordersSubmitDTO);
}
