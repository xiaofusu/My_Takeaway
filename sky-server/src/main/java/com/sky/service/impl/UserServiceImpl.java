package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @Author wzy
 * @Date 2023/11/1 13:30
 * @description: 微信用户实现
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

   //微信接口地址
    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO);
        //判断openID是否为空 如果为空则登录失败 抛出业务异常
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //如果不为空 则判断当前用户是否为新用户（查询数据库）
        User user = userMapper.getByOpenid(openid);
        // 是新用户 则完成自动注册
        if(user==null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now()).build();
            userMapper.insert(user);
        }
        return user;
    }

    /**
     * 调用微信接口服务 获取微信唯一标识openid
     * @param userLoginDTO
     * @return
     */
    private String getOpenid(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获得当前微信用户的openid
        HashMap<String, String> hash = new HashMap<>();
        hash.put("appid",weChatProperties.getAppid());
        hash.put("secret",weChatProperties.getSecret());
        hash.put("js_code", userLoginDTO.getCode());
        hash.put("grant_type","authorization_code");
        //请求返回的数据时json格式
        String json = HttpClientUtil.doGet(WX_LOGIN, hash);
        //使用fastjson解析json字符串
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
