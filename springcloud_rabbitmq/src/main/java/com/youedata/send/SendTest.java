package com.youedata.send;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liyanlu on 2018/1/25.
 */
@Component
public class SendTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(){
        rabbitTemplate.convertAndSend("hello","你好");
    }
}
