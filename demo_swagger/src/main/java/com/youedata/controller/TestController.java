package com.youedata.controller;

import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: lyl
 * @date: 2018/10/8 11:17.
 */
@RequestMapping("/test")
@Controller
@Api(value = "/test", tags = "测试", description = "这是一个测试")
public class TestController {

    @RequestMapping("/sayHello/{name}")
    @ResponseBody
    @ApiOperation(
            value = "测试", notes = "这是一个测试",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "请输入姓名"),
            @ApiResponse(code = 404, message = "姓名不存在")}
    )
    public String test(@ApiParam(value = "姓名", required = true)
                           @PathVariable("name") String name) throws Exception {
        String result="hello "+name;
        System.out.println("hello "+name);
        if("".equals(name)){
            throw new Exception("请输入姓名");
        }else if("zs".equals(name)){
            throw new Exception(name+"不存在");
        }
        return result;
    }
}
