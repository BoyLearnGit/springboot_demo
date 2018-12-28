package com.youedata.controller;

import com.youedata.send.SendTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by liyanlu on 2018/1/25.
 */
@Controller
public class TestController {
    @Autowired
    private SendTest sendTest;

    @RequestMapping("/test")
    public void test(){
        sendTest.send();
    }
}
