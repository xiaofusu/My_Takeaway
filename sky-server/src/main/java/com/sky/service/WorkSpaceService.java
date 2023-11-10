package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;

/**
 * @Author wzy
 * @Date 2023/11/10 8:37
 * @description: 管理端工作台业务接口
 */
public interface WorkSpaceService {
    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO getOverviewOrders();

    /**
     * 查询今日运营数据
     * @return
     */
    BusinessDataVO getBusinessData();
}
