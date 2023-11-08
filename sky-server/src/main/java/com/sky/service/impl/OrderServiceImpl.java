package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author wzy
 * @Date 2023/11/5 12:40
 * @description: 用户订单实现
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;//订单
    @Autowired
    private OrderDetailMapper orderDetailMapper;//订单明细
    @Autowired
    private AddressBookMapper addressBookMapper;//地址簿
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;//购物车
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebSocketServer webSocketServer;
//    @Autowired
//    private WeChatPayUtil weChatPayUtil;
    @Value("${sky.shop.address}")
    private String shopAddress;
    @Value("${sky.baidu.ak}")
    private String ak;


    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO OrderSubmit(OrdersSubmitDTO ordersSubmitDTO) {
        //各种业务异常（地址为空，购物车为空）
        AddressBook addressBook = addressBookMapper.getAddressById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //检查用户的地址是否超过了配送范围
        checkOutOfRange(addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());

        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getListByUserId(BaseContext.getCurrentId());
        if(shoppingCartList==null && shoppingCartList.size()==0){
            //抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());//下单时间
        orders.setPayStatus(Orders.UN_PAID);//订单支付状态 刚下单时默认支付状态为未支付
        orders.setStatus(Orders.PENDING_PAYMENT);//订单状态 刚下单默认代付款状态
        orders.setNumber(String.valueOf(System.currentTimeMillis()));//设置订单号 当前时间的时间戳
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());//收货人
        orders.setUserId(BaseContext.getCurrentId());

        //地址
        String address = addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail();
        orders.setAddress(address);

        orderMapper.insert(orders);
        //向订单明细表插入n条数据
        //遍历购物车
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());//设置当前订单明细关联的订单id
            orderDetailList.add(orderDetail);
        }
        //批量插入
        orderDetailMapper.insertBatch(orderDetailList);

        //清空当前用户购物车
        shoppingCartMapper.deleteAll(BaseContext.getCurrentId());
        //封装OrderSubmitVO 返回数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount()).build();
        return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );

        //模拟支付请求
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //通过websocket向客户端浏览器推送消息  type orderId content
        HashMap hashMap = new HashMap();
        hashMap.put("type",1);//1表示来单提醒 2.表示用户催单
        hashMap.put("orderId",ordersDB.getId());
        hashMap.put("content","订单号："+outTradeNo);

        String jsonString = JSON.toJSONString(hashMap);
        webSocketServer.sendToAllClient(jsonString);
    }

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetailById(Long id) {
        Orders order = orderMapper.getById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        //查询订单明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cancelOrder(Long id) {
        //先查询订单是否存在
        Orders order = orderMapper.getById(id);
        if(order==null){
            //订单不存在 抛出异常
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        //查询订单状态 当订单商家已接单或者派送已完成已取消不能取消
        switch (order.getStatus()){
            case 3: throw new OrderBusinessException("商家已接单,无法取消");
            case 4: throw new OrderBusinessException("订单派送中,无法取消");
            case 5: throw new OrderBusinessException("订单已完成,无法取消");
            case 6: throw new OrderBusinessException("订单已取消,无法取消");
        }
//        // 订单处于待接单状态下取消，需要进行退款
//        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
//            //调用微信支付退款接口
//            weChatPayUtil.refund(
//                    order.getNumber(), //商户订单号
//                    order.getNumber(), //商户退款单号
//                    new BigDecimal(0.01),//退款金额，单位 元
//                    new BigDecimal(0.01));//原订单金额
//
//            //支付状态修改为 退款
//            order.setPayStatus(Orders.REFUND);
//        }

        // 订单处于待接单状态下取消，需要进行退款
        if(order.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            //模拟退款 直接设置支付状态为退款
            order.setPayStatus(Orders.REFUND);
        }
        // 更新订单状态、取消原因、取消时间
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("用户取消");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    /**
     * 分页查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult historyOrdersPage(OrdersPageQueryDTO ordersPageQueryDTO) {
        //开始分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());//设置用户id
        //条件查询
        Page<Orders> page =  orderMapper.pageQuery(ordersPageQueryDTO);

        List<Orders> orderList = page.getResult();
        List<OrderVO> orderVOList = null;

       orderVOList = orderList.stream().map((order -> {
           OrderVO orderVO = new OrderVO();
           BeanUtils.copyProperties(order,orderVO);
           //查询订单对应的订单明细信息
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderVO.getId());
            orderVO.setOrderDetailList(orderDetailList);
            return orderVO;
        })).collect(Collectors.toList());
        return new PageResult(page.getTotal(),orderVOList);
    }

    @Override
    public void repetition(Long id) {
        //查询出此订单的订单详情数据
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        //将订单详情对象转为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart,"id");
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(shoppingCartList);

    }

    /**
     * 分页条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageConditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //查询订单信息
        Page<Orders> ordersPage = orderMapper.pageQuery(ordersPageQueryDTO);
        //订单包含的菜品，以字符串形式展示 返回OrderVO对象
        List<Orders> ordersList = ordersPage.getResult();
        List<OrderVO> orderVOList = new ArrayList<>();
        if(ordersList!=null&&ordersList.size()>0){
           orderVOList = ordersList.stream().map((order -> {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
               StringBuffer orderDishes = getStringBuffer(order);//据订单id获取菜品信息字符串
               orderVO.setOrderDishes(String.valueOf(orderDishes));
                return orderVO;

            })).collect(Collectors.toList());
        }

        return new PageResult(ordersPage.getTotal(),orderVOList);
    }
    /**
     * 据订单id获取菜品信息字符串
     * @param order
     * @return
     */
    private StringBuffer getStringBuffer(Orders order) {
        //根据orderId查询相应的订单详情表中的菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
        //遍历菜品集合 获得菜品名字 然后拼接成字符串
        StringBuffer orderDishes = new StringBuffer();
        orderDetailList.forEach(orderDetail -> {
            String dishName = orderDetail.getName();//菜品名字
            Integer number = orderDetail.getNumber();//菜品数量
            orderDishes.append(dishName + "*" + number + " ");
        });
        return orderDishes;
    }

    /**
     * 获取订单各个状态的数量
     * @return
     */
    @Override
    public OrderStatisticsVO GetStatistics() {
        //根据状态分别查询出待接单、待派送、派送中的订单数量
        Integer number1 = orderMapper.getCountByStatus(Orders.TO_BE_CONFIRMED);//待接单数量
        Integer number2 = orderMapper.getCountByStatus(Orders.CONFIRMED);//已接单数量
        Integer number3 = orderMapper.getCountByStatus(Orders.DELIVERY_IN_PROGRESS);//待接单数量

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(number1);
        orderStatisticsVO.setConfirmed(number2);
        orderStatisticsVO.setDeliveryInProgress(number3);
        return orderStatisticsVO;
    }

    /**
     * 商家接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders order = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED).build();
        orderMapper.update(order);
    }

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {

        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

//        //支付状态
//        Integer payStatus = ordersDB.getPayStatus();
//        if (payStatus == Orders.PAID) {
//            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);
//        }
        // 拒单需根据订单id更新订单状态、拒单原因、取消时间
        Orders order = Orders.builder().id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now()).build();
        orderMapper.update(order);
    }

    /**
     * 订单派送
     * @param id
     */
    @Override
    public void delivery(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为3
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder().id(id)
                .status(Orders.DELIVERY_IN_PROGRESS).build();
        orderMapper.update(orders);
    }

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());
//        //用户已支付 需要退款
//        if(ordersDB.getPayStatus()==1){
//            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);
//        }
        // 校验订单是否存在 判断订单是否已接单 接单才能取消
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders order = Orders.builder().id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    /**
     * 订单完成
     * @param id
     */
    @Override
    public void complete(Long id) {
        //个根据id查询订单
        Orders orderDB = orderMapper.getById(id);
        if(orderDB == null || !orderDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders order = Orders.builder().id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    /**
     * 用户催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        //根据id查询订单
        Orders orderDB = orderMapper.getById(id);
        if(orderDB==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);                     
        }
        HashMap hashMap = new HashMap();
        //通过websocket向客户端浏览器推送消息  type orderId content
        hashMap.put("type",2);
        hashMap.put("orderId",id);
        hashMap.put("content","订单号："+orderDB.getNumber());
        String jsonString = JSON.toJSONString(hashMap);
        webSocketServer.sendToAllClient(jsonString);

    }

    /**
     * 检查客户的收回地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address){
        HashMap<String,String> map = new HashMap<>();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);
        //获取店铺的经纬度坐标 发送请求
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        //地址及解析
        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺地址解析失败");
        }
       /* {
            "status": 0,
            "result": {
                "location": {
                    "lng": 116.30762232672,
                    "lat": 40.056828485961
                    },
                "precise": 1,
                "confidence": 80,
                "comprehension": 100,
                "level": "门址"
            }
        }*/
        //获得经纬度
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }
       /* "result": {
            "origin": {
                "lng": 116.339303,
                "lat": 40.011160069003
            },
            "destination": {
                "lng": 116.452562,
                "lat": 39.93640407532
            },
            "routes": [
                {
                    "route_md5": "17202c2a9d15a5e9d8a038bae8f93a35",
                        "distance": 17596,
                        "duration": 2045,
                        "traffic_condition": 2,
                        "toll": 0,
                        "restriction_info": {
                    "status": 0
                },*/
        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }

    }

}
