package com.youedata.controller;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.agent.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by liyanlu on 2017/12/1.
 */
//@RestController
//@EnableAutoConfiguration
//@ComponentScan
//@RefreshScope
@RestController
public class TestController {
    @Autowired
    private RestTemplate restTemplate;
//    @Value("${from}")
//    private String from;

    @RequestMapping("/from")
    public String from() {
        ConsulRawClient client = new ConsulRawClient("localhost", 8500);
        ConsulClient consul = new ConsulClient(client);
        //获取所有服务
        Map<String, Service> map = consul.getAgentServices().getValue();
        Service service=map.get("consul-1341");
        return restTemplate.getForEntity("http://"+service.getAddress()+":"+service.getPort()+"/test/say",String.class).getBody();
    }

    @RequestMapping("hello")
    public String hello(){
        return "hello world";
    }

    public static void main(String[] args) {

    }
}
