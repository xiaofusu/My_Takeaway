package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/10/22 21:35
 * @description: 套餐信息mapper
 */
@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from sky_take_out.setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根绝id获取套餐
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.setmeal where id = #{id}")
    SetmealVO getById(Long id);

    /**
     * 修改菜品
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id获得套餐名字
     * @param setmealId
     * @return
     */
    @Select("select name from sky_take_out.setmeal where id = #{setmealId}")
    String getNameById(Long setmealId);

    /**
     * 根据状态查询套餐数量
     * @param status
     * @return
     */
    @Select("select count(id) from sky_take_out.setmeal where status  = #{status}")
    Integer countByStatus(int status);
}
