package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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
    void insert(Category category);

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类状态
     * @param category
     */
    void update(Category category);
}
