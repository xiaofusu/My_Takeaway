package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 * SpringBoot提供了全局异常捕获注解@ControllerAdvice
 *首先定义一个全局异常捕获类GlobalExceptionHandler，加上注解ControllerAdvice
 *  @RestControllerAdvice
 * 如果用了它，错误处理方法的返回值不会表示用的哪个视图，而是会作为HTTP body处理，即相当于错误处理方法加了@ResponseBody注解。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error("异常信息：{}", ex.getMessage());
        if(ex.getMessage().contains("#23000")){//#23000 数据库错误
            return Result.error("用户已存在");
        }else{
            return Result.error(ex.getMessage());
        }
    }
}
