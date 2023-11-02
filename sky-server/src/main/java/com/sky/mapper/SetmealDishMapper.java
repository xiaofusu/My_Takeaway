package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/10/25 15:25
 * @description: 套餐mapper
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
     *
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐对应的菜品信息
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select * from sky_take_out.setmeal_dish where setmeal_id = #{setmealId} ")
    List<SetmealDish> getBySetmealId(Long setmealId);

    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 批量删除套餐对应的菜品信息
     * @param setmealIds
     */
    void deleteBatch(List<Long> setmealIds);

    /**
     * 根据套餐套餐id查询里面的菜品信息
     * @param setmealId
     * @return
     */
    @Select("select sd.name ,sd.copies,d.image,d.description from sky_take_out.setmeal_dish sd " +
            "left join sky_take_out.dish d on d.id = sd.dish_id where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemVO(Long setmealId);
}

