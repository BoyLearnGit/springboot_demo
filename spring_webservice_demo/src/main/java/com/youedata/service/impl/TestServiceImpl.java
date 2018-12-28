package com.youedata.service.impl;

import com.youedata.model.User;
import com.youedata.service.TestService;

import javax.jws.WebService;

/**
 * @author: lyl
 * @date: 2018/5/2 10:19.
 */
@WebService(targetNamespace="http://service.youedata.com/",endpointInterface = "com.youedata.service.TestService")
public class TestServiceImpl implements TestService{
    @Override
    public String getName(String userId) {
        return "李四";
    }

    @Override
    public User getUser(String userId) {
        User user=new User();
        user.setUserId(userId);
        user.setUserName("李四");
        return user;
    }
}
