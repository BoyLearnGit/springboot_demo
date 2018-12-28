package com.youedata.receive;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by liyanlu on 2018/1/25.
 */
@Component
@RabbitListener(queues = "hello")
public class ReceiveTest {
    @RabbitHandler
    public void process(String hello){
        System.out.println(hello);

    }
}
