package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wzy
 * @Date 2023/11/10 8:38
 * @description: 管理端工作台业务实现
 */
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 查询当天订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {
        Map map = new HashMap();
        map.put("begin",LocalDateTime.now().with(LocalTime.MIN));

        //待接单数量
        //private Integer waitingOrders;
        map.put("status",Orders.REFUND);
        Integer  waitingOrders = orderMapper.getCountByMap(map);
        //待派送数量
        //private Integer deliveredOrders;
        map.put("status",Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.getCountByMap(map);
        //已完成数量
        //private Integer completedOrders;
        map.put("status",Orders.COMPLETED);
        Integer completedOrders = orderMapper.getCountByMap(map);
        //已取消数量
        //private Integer cancelledOrders;
        map.put("status",Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.getCountByMap(map);
        //全部订单
        //private Integer allOrders;
        map.put("status",null);
        Integer allOrders = orderMapper.getCountByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询今日运行数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {
        Map map = new HashMap();
        map.put("begin",LocalDateTime.now().with(LocalTime.MIN));//当前时间(今日零点）
        //查询总订单数
        Integer totalOrderCount = orderMapper.getCountByMap(map);

        //private Double turnover;//营业额
        map.put("status",Orders.COMPLETED);
        BigDecimal turnover = orderMapper.sumAmoutByMap(map);
        turnover = turnover == null ? new BigDecimal(0.0) : turnover;

        //private Integer validOrderCount;//有效订单数
        Integer validOrderCount = orderMapper.getCountByMap(map);

        //private Double orderCompletionRate;//订单完成率
        Double orderCompletionRate = 0.0;
        //private Double unitPrice;//平均客单价 营业额 / 有效订单数
        Double unitPrice = 0.0;
        if(totalOrderCount!=0){
            orderCompletionRate = validOrderCount.doubleValue()/totalOrderCount;
            unitPrice = turnover.doubleValue()/validOrderCount;
        }

        //private Integer newUsers;//新增用户数
        Integer newUsers = userMapper.getCountByMap(map);
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }
}
