package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/4 12:08
 * @description: 地址簿dao层
 */
@Mapper
public interface AddressBookMapper {
    /**
     * 新增地址
     * @param addressBook
     */
    void insert(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址信息
     * @param userId
     * @return
     */
    @Select("select * from sky_take_out.address_book where user_id = #{userId}")
    List<AddressBook> list(Long userId);
}
