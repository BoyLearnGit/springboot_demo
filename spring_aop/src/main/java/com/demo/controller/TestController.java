package com.demo.controller;

import com.demo.aop.Fly;
import com.demo.aop.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: lyl
 * @date: 2018/12/5 14:04.
 */
@Controller
public class TestController {
    @Autowired
    private TestService testService;

    @ResponseBody
    @RequestMapping("test")
    public String test(){
        System.out.println("hello world");
        Fly fly=(Fly)testService;
        System.out.println(fly.fly());
        return "hello world";
    }
}
