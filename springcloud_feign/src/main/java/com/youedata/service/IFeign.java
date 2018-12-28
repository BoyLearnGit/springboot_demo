package com.youedata.service;

import com.youedata.hystrix.TestHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by liyanlu on 2017/11/30.
 */
@FeignClient(value="a",fallback = TestHystrix.class)
public interface IFeign {
    @RequestMapping("test/say")
    String say();
}
