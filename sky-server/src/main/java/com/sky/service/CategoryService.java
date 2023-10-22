package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;

/**
 * @Author wzy
 * @Date 2023/10/22 17:47
 * @description: 分类接口
 */
public interface CategoryService {
    /**
     * 新增分类
     * @param categoryDTO
     */
    void add(CategoryDTO categoryDTO);

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void status(Integer status, Long id);
}
