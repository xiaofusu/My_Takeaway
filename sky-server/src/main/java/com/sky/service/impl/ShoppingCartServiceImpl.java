package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/3 15:06
 * @description: 购物车
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //当添加购物车时 先判断此商品是否在购物车中  如果已经在 则直接修改操作，数量+1
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //前端请求带的token中有userid 被拦截器拦截已经设置到当前线程中去
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //条件查询购物车中是否已经有了要添加的商品 select * from shopping_cart where user_id=? dish_id=?  setmeal_id=? dishflavor=? u
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list!=null&&list.size()>0){
            //购物车中有此菜品
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            //修改 update shopping_cart set number = ? where id = ?
            shoppingCartMapper.updateNumberById(cart);
        }else{
            //如果不存在则插入购物车
            Long dishId = shoppingCartDTO.getDishId();
            //判断插入购物车的是菜品还是套餐
            if(dishId!=null){
                //此时插入的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                //此时是套餐
                SetmealVO setmealVO = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setAmount(setmealVO.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCartList() {
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getListByUserId(BaseContext.getCurrentId());
        return shoppingCartList;
    }

    /**
     * 删除购物车一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //先判断此菜品在购物车的数量
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //前端请求带的token中有userid 被拦截器拦截已经设置到当前线程中去
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        Integer count = list.get(0).getNumber();//菜品数量
        if(count>1){
            //直接修改菜品的数量减一
            list.get(0).setNumber(count-1);
            shoppingCartMapper.updateNumberById(list.get(0));
        }else{
            //删除菜品
            shoppingCartMapper.deleteById(shoppingCart);
        }
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteAll(BaseContext.getCurrentId());
    }
}
