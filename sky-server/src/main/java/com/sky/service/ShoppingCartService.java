package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/3 15:05
 * @description: 购物车
 */
public interface ShoppingCartService {
    /**
     * 添加到购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> getShoppingCartList();

    /**
     * 删除购物车一个商品
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void cleanShoppingCart();
}
