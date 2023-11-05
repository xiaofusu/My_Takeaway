package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.ShortBufferException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
