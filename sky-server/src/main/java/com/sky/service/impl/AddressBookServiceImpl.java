package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author wzy
 * @Date 2023/11/4 12:08
 * @description: 地址簿业务层实现类
 */
@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void addAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());//设置用户id
        addressBook.setIsDefault(StatusConstant.DISABLE);//设置是否为默认地址 刚新增地址0表示不是默认地址
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @Override
    public List<AddressBook> list() {
        List<AddressBook> addressBookList = addressBookMapper.list(BaseContext.getCurrentId());
        return addressBookList;
    }
}
