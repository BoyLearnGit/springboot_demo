package com.youedata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;

/**
 * Created by liyanlu on 2017/12/7.
 */
@RestController
public class TestController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private DiscoveryClient discoveryClient;
    @RequestMapping("test/say")
    public String say(){
        return "hello world!";
    }

    @RequestMapping("test/hello")
    public String hello(){
//        List<ServiceInstance> services=discoveryClient.getInstances("test");
//        String url = "http://" + services.get(0).getHost() + ":" + services.get(0).getPort() + "/hello";
//        System.out.println(url);
//        return restTemplate.getForObject(url,String.class);
        return restTemplate.getForObject("http://test/hello",String.class);
    }

}
