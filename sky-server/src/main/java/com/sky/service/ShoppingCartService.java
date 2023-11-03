package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

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
}
