package com.youedata.hystrix;

import com.youedata.service.IFeign;
import org.springframework.stereotype.Component;

/**
 * 断路器
 * Created by liyanlu on 2017/11/30.
 */
@Component
public class TestHystrix implements IFeign{
    @Override
    public String say() {
        return "连接失败";
    }
}
