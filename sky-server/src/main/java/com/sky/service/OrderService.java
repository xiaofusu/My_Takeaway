package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

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

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 根据id查询订单信息
     * @param id
     * @return
     */
    OrderVO getOrderDetailById(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 分页查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult historyOrdersPage(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 分页条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
}
