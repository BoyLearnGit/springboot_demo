package com.example.demo;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class DemoApplication {

	public static void main(String[] args) throws Exception {
		JaxWsDynamicClientFactory dcf =JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client client =dcf.createClient("http://localhost:8080/test/user?wsdl");
		//getUser 为接口中定义的方法名称  张三为传递的参数   返回一个Object数组
		Object[] objects=client.invoke("getUser","411001");
		//输出调用结果
		System.out.println("*****"+objects[0].toString());

		SpringApplication.run(DemoApplication.class, args);
	}
}
