package com.youedata.controller;

import com.youedata.service.CodeInfo;
import com.youedata.service.IRedisService;
import com.youedata.service.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liyanlu on 2018/1/25.
 */
@Controller
public class TestController {


    @Autowired
    private RedisServiceImpl redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("get")
    @ResponseBody
    public String getValue(String key){

        return redisService.get(key).toString();
    }

    @ResponseBody
    @RequestMapping("put")
    public String putValue(){
        CodeInfo c=new CodeInfo();
        c.setCodeKey("123");
        c.setCodeVal("123");
        c.setGmtCreate(new Date());
        redisService.setRedisKey("123");
        redisService.put("123",c,-1);
        return "success";
    }

    @ResponseBody
    @RequestMapping("test")
    public Map test(){
        Map map=new HashMap();
//        redisTemplate.set
        return map;
    }

}
