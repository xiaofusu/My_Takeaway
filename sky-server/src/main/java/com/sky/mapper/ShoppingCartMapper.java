package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/3 15:06
 * @description: 购物车mapper
 */
@Mapper
public interface ShoppingCartMapper {
    /**
     * 动态条件查询购物车数据
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 修改操作 商品数量加1
     * @param cart
     */
    @Update("update sky_take_out.shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart cart);

    /**
     * 加入购物车数据
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 查看购物车
     * @param userId
     * @return
     */
    @Select("select * from sky_take_out.shopping_cart where user_id = #{userId}")
    List<ShoppingCart> getListByUserId(Long userId);

    /**
     * 条件删除购物车中的菜品或套餐
     * @param shoppingCart
     */
    void deleteById(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     * @param userId
     */
    @Delete("delete  from sky_take_out.shopping_cart where user_id = #{userId}")
    void deleteAll(Long userId);

    /**
     * 批量插入购物车
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
