package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/4 12:05
 * @description: 用户下单地址
 */
@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端地址簿相关接口")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping()
    @ApiOperation("新增地址")
    public Result addAddress(@RequestBody AddressBook addressBook){
        log.info("新增地址为：{}",addressBook);
        addressBookService.addAddress(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list(){
        List<AddressBook> addressBookList = addressBookService.list();
        return Result.success(addressBookList);
    }
}
