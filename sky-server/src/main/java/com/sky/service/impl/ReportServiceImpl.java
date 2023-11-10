package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlCursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @Resource
    private WorkSpaceService workSpaceService;
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
            Map map = new HashMap();
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

            //统计每天新增数量：select count(id) from user where create_time > beginTim,.e and create_time < endTime
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

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        //1.定义一个集合用于存放begin到end之间每天的日期
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);//加一天
            dateList.add(begin);
        }
        //存放每日订单数
        ArrayList<Integer> orderCountList = new ArrayList<>();
        //存放每日有效订单数
        ArrayList<Integer> validCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //每日订单数 select count(id) from orders where order_time > beginTime and order_time < endTime
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer orderCount = orderMapper.getCountByMap(map);
            orderCountList.add(orderCount);
            //每日有效订单数 select count(id) from orders where order_time > beginTime and order_time < endTime and status = 5
            map.put("status",Orders.COMPLETED);
            Integer validCount = orderMapper.getCountByMap(map);
            validCountList.add(validCount);
        }

        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();//订单总数
        Integer validOrderCount = validCountList.stream().reduce(Integer::sum).get();//有效订单数
        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount!= 0){
            orderCompletionRate = validOrderCount.doubleValue()/totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计指定区间销量前10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO SalesTop10(LocalDate begin, LocalDate end) {
        //将年月日转为年月日时分秒
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        Map map = new HashMap();
        map.put("status",Orders.COMPLETED);
        map.put("begin",beginTime);
        map.put("end",endTime);
        List<GoodsSalesDTO> saleTop10List = orderMapper.getSaleTop10(map);
        //定义商品名字列表 用于存放商品名字
        ArrayList<String> nameList = new ArrayList<>();
        //定于销售数量列表 用于存放对应商品的销售数量
        ArrayList<Integer> numberList = new ArrayList<>();
        //遍历集合saleTop10List 封装成SalesTop10ReportVO返回
        for (GoodsSalesDTO goodsSalesDTO : saleTop10List) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    /**
     * 导出营业数据报表
     * @param response
     */
    @Override
    public void exportData(HttpServletResponse response) {
        //查询数据库 获得营业数据 查询最近三十天的数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workSpaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //通过Apache POI 将数据写入Excel表格中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //基于模板文件创建Excel文件
        ServletOutputStream out = null;
        XSSFWorkbook excel = null;
        try {
            excel = new XSSFWorkbook(in);
            //填充数据
            XSSFSheet sheet1 = excel.getSheet("Sheet1");//获得标签页
            //getRow获得第二行 getcell获得第二个单元格 setCellValue填写数据
            sheet1.getRow(1).getCell(1).setCellValue("时间："+begin+"-"+end);

            //第四行
            XSSFRow row4 = sheet1.getRow(3);
            //第三个单元格填写营业额
            row4.getCell(2).setCellValue(String.valueOf(businessData.getTurnover()));
            //第五个单元格填写订单完成率
            row4.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            //第7个单元格填写订单完成率
            row4.getCell(6).setCellValue(businessData.getNewUsers());

            //第五行
            XSSFRow row5 = sheet1.getRow(4);
            //第三个单元格填写有效订单
            row5.getCell(2).setCellValue(businessData.getValidOrderCount());
            //第五个单元格填写平均客单价
            row5.getCell(4).setCellValue(businessData.getUnitPrice());

            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                BusinessDataVO businessEveryDayData = workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //获得某一行
                XSSFRow row = sheet1.getRow(i + 7);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(String.valueOf(businessEveryDayData.getTurnover()));//营业额
                row.getCell(3).setCellValue(businessEveryDayData.getValidOrderCount());//有效订单
                row.getCell(4).setCellValue(businessEveryDayData.getOrderCompletionRate());//订单完成率
                row.getCell(5).setCellValue(businessEveryDayData.getUnitPrice());//平均客单价
                row.getCell(6).setCellValue(businessEveryDayData.getNewUsers());//新增用户

            }

            //通过输出流将Excel文件下载到客户端浏览器
            out = response.getOutputStream();
            excel.write(out);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                out.close();
                excel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
