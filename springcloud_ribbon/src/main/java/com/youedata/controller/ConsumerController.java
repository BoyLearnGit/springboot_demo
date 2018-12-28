package com.youedata.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by liyanlu on 2017/11/30.
 */
@RestController
@RequestMapping("consumer")
public class ConsumerController {
    @Autowired
    public RestTemplate restTemplate;

//    @HystrixCommand(fallbackMethod = "addServiceFallback")
//    public String addService() {
//        return restTemplate.getForEntity("http://a/test/say", String.class).getBody();
//    }

    public String addServiceFallback() {
        return "error";
    }

    /**
     * @HystrixCommand(fallbackMethod = "addServiceFallback")断路器
     * @return
     */
    @RequestMapping("test")
    @HystrixCommand(fallbackMethod = "addServiceFallback")
    public String test(){
       return restTemplate.getForEntity("http://a/test/say",String.class).getBody();
    }
}
