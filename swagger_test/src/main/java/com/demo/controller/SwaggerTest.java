package com.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: lyl
 * @date: 2018/12/10 10:32.
 */
@Api("swagger测试")
@Controller
public class SwaggerTest {


    @RequestMapping("test")
    @ResponseBody
    @ApiOperation(value = "测试",notes = "测试")
    @ApiImplicitParam(name="name",value = "姓名",paramType = "",required = true,dataType = "String")
    public String test(String name){
        return "hello world!"+name;
    }
}
