package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author wzy
 * @Date 2023/11/1 14:13
 * @description: 微信用户mapper
 */
@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from sky_take_out.user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户信息
     * @param user
     */
    void insert(User user);

    /**
     * 根据主键id查用户
     * @param userId
     * @return
     */
    @Select("select * from sky_take_out.user where id = #{id}")
    User getById(Long userId);

    /**
     * 条件查询用户数量
     * @param map
     * @return
     */
    Integer getCountByMap(Map map);
}
