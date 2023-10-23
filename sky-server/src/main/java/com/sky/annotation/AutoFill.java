package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author wzy
 * @Date 2023/10/23 14:11
 * @description: 自定义注解 用于标识某个方法需要进行公共字段填充
 */
@Target(ElementType.METHOD)//指定注解只能加在方法上面
////通过@Retention定义注解的生命周期，RetentionPolicy.RUNTIME : 始终不会丢弃，可以使用反射获得该注解的信息。自定义的注解最常用的使用方式
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操纵类型:UPDATE INSERT
    OperationType value();
}
