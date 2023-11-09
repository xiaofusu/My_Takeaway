package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wzy
 * @Date 2023/11/9 14:05
 * @description: 数据统计实现
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     *
     * 指定区间内营业额数据统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        //private String dateList;
        //营业额，以逗号分隔，例如：406.0,1520.0,75.0
        //private String turnoverList;

        //1.定义一个集合用于存放begin到end之间每天的日期
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);//加一天
            dateList.add(begin);
        }

        ArrayList<BigDecimal> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {//遍历日期，查询出每一天的营业额
            //营业额：查询当前日期下所有已完成订单金额的总和
            //select sum(amount) from orders where order_time > beginTime and order_time < endTime and status = 5
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            BigDecimal turnover = orderMapper.sumAmoutByMap(map);
            turnover = (turnover == null) ? new BigDecimal("0.0") : turnover;
            turnoverList.add(turnover);
        }

        String list = StringUtils.join(dateList, ",");//调用工具类将list以逗号分割转为字符串


        return TurnoverReportVO.builder()
                .dateList(list)
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /**
     * 根据指定区间统计新增用户
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //1.定义一个集合用于存放begin到end之间每天的日期
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);//加一天
            dateList.add(begin);
        }
        ArrayList<Integer> newUserList = new ArrayList<>();//每天新增用户数

        ArrayList<Integer> totalUserList = new ArrayList<>();//每天总用户数
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //统计每天总的用户数量：select count(id) from user where create_time <endTime
            Map map = new HashMap();
            map.put("end",endTime);
            Integer totalUser = userMapper.getCountByMap(map);
            totalUserList.add(totalUser);

            //统计每天新增数量：select count(id) from user where create_time > beginTime and create_time < endTime
            map.put("begin",beginTime);
            Integer newUser = userMapper.getCountByMap(map);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }
}
