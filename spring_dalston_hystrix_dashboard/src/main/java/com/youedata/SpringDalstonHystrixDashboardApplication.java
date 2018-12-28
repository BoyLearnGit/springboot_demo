package com.youedata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrixDashboard
public class SpringDalstonHystrixDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDalstonHystrixDashboardApplication.class, args);
	}
}
