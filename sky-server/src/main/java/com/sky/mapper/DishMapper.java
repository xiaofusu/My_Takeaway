package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author wzy
 * @Date 2023/10/22 21:12
 * @description: 菜品mapper
 */
@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询此分类下的菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from sky_take_out.dish  where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
