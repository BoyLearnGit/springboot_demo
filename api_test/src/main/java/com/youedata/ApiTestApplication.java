package com.youedata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@Controller
public class ApiTestApplication {

	@RequestMapping("test")
	@ResponseBody
	public String test(){
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "api压力测试";
	}

	public static void main(String[] args) {
		HashMap map=new HashMap();
		ConcurrentHashMap map1=new ConcurrentHashMap();
		Hashtable table=new Hashtable();
		SpringApplication.run(ApiTestApplication.class, args);
	}
}
