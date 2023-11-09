package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author wzy
 * @Date 2023/11/5 12:41
 * @description: 用户订单
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from sky_take_out.orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 分页查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据状态查询订单数目
     * @param status
     * @return
     */
    @Select("select count(id) from sky_take_out.orders where status = #{status}")
    Integer getCountByStatus(Integer status);

    /**
     *查询昨天处于派送中还未完成的订单
     * @param status
     * @param now
     */
    @Select("select * from sky_take_out.orders where status = #{status} and order_time < #{now}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime now);

    /**
     * 动态查询营业额数据
     * @param map
     * @return
     */
    BigDecimal sumAmoutByMap(Map map);

    /**
     * 根据条件查询订单数目
     * @param map
     * @return
     */
    Integer getCountByMap(Map map);

    /**
     * 获得销量top10
     * @param map
     * @return
     */
   List<GoodsSalesDTO> getSaleTop10(Map map);

}
