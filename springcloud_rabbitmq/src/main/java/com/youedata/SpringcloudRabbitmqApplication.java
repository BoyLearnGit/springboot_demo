package com.youedata;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringcloudRabbitmqApplication {

	@Bean
	public Queue hello(){
		return  new Queue("hello");
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringcloudRabbitmqApplication.class, args);
	}
}
