package com.youedata.controller;

import com.youedata.service.IFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liyanlu on 2017/11/30.
 */
@RestController
public class TestController {

    @Autowired
    public IFeign iFeign;
    @RequestMapping("test")
    public String say(){
       return iFeign.say();
    }
}
