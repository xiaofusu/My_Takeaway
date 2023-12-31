package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/10/22 17:48
 * @description: 分类mapper层
 */
@Mapper
public interface CategoryMapper {
    /**
     * 新增员工
     * @param category
     */
    @Insert("insert into sky_take_out.category( type, name, sort, status, create_time, " +
            "update_time, create_user, update_user) values (#{type},#{name},#{sort},#{status}," +
            "#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Category category);

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类状态
     * @param category
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);


    /**
     * 根据id删除
     * @param id
     */
    @Delete("delete from sky_take_out.category where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);

    /**
     * 根据分类id查询分类名称
     * @param categoryId
     * @return
     */
    @Select("select name from sky_take_out.category where id = #{categoryId}")
    String queryNameById(Long categoryId);
}
