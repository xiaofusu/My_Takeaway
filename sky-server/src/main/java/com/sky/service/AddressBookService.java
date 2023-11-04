package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/4 12:07
 * @description: 地址簿
 */
public interface AddressBookService {
    /**
     * 新增地址
     * @param addressBook
     */
    void addAddress(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    List<AddressBook> list();

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getAddressById(Long id);

    /**
     * 修改地址
     * @param addressBook
     */
    void updateAddress(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);
}
