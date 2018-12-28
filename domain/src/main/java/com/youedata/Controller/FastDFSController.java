package com.youedata.Controller;


import com.youedata.config.TestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by liyanlu on 2017/12/19.
 */
@Controller
public class FastDFSController {
    @Value("${test.name}")
    private String name;

    @Autowired
    private TestProperties testProperties;
    @RequestMapping("test")
    public void getName(){
        System.out.println(testProperties.getName());
        System.out.println(name);
    }

}
