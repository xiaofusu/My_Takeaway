package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/2 10:59
 * @description: 用户端分类查询
 */
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "C端分类接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/list")
    @ApiOperation("分类查询")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
