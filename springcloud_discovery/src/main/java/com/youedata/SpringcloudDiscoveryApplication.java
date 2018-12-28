package com.youedata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
public class SpringcloudDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringcloudDiscoveryApplication.class, args);
	}
}
