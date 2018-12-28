package com.youedata.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liyanlu on 2017/11/30.
 */
@RestController
@RequestMapping("test")
public class TestController {
    @RequestMapping("say")
    public String say(){
        System.out.println("hi,i'm lyl");
        return "hello world!";
    }
}
