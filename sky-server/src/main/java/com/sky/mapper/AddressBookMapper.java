package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.address_book where id = #{id}")
    AddressBook getAddressById(Long id);

    /**
     * 修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 将当前用户下的所有地址修改为非默认地址
     * @param userId
     */
    @Update("update sky_take_out.address_book set is_default = 0 where user_id = #{userId}")
    void updateIsDefaultByUserId(Long userId);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("delete from sky_take_out.address_book where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据用户id查询对应的默认地址
     * @param userId
     * @return
     */
    @Select("select * from sky_take_out.address_book where user_id = #{userId} and is_default = 1")
    AddressBook getDefaultAddressByUserId(Long userId);
}
