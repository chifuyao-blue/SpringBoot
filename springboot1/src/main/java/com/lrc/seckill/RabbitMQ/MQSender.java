package com.lrc.seckill.RabbitMQ;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 类的简要描述
 *
 * @author 33182
 * @Description: 消息发送者
 * @date 2024/10/25 17:50
 */
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

  /*  public void send(Object msg){
        log.info("发送消息："+ msg);
        rabbitTemplate.convertAndSend("fanoutExchange","",msg);
    }

    //direct
    public void send01(Object msg){
        log.info("发送red消息："+msg);
        rabbitTemplate.convertAndSend("directExchange","queue.red",msg);
    }

    public void send02(Object msg){
        log.info("发送绿色消息："+msg);
        rabbitTemplate.convertAndSend("directExchange","queue.green",msg);
    }

    //topic模式
    public void send03(Object msg){
        log.info("发送消息给QUEUE01: "+ msg);
        rabbitTemplate.convertAndSend("topicExchange","queue.red.massage",msg);
    }

    public void send04(Object msg){
        log.info("发送给2个QUEUE："+msg);
        rabbitTemplate.convertAndSend("topicExchange","message.queue.green.abc",msg);
    }

    //Headers模式
    public void send05(String msg){
        log.info("发送消息（被2个QUEUE接收）："+msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color","red");
        properties.setHeader("speed","fast");
        Message message = new Message(msg.getBytes(),properties);
        rabbitTemplate.convertAndSend("headersExchange","",message);
    }
    public void send06(String msg){
        log.info("发送消息queue01接收："+msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color","red");
        properties.setHeader("speed","normal");
        Message message = new Message(msg.getBytes(),properties);
        rabbitTemplate.convertAndSend("headersExchange","",message);
    }*/

    //发送秒杀信息
    public void sendSeckillMessage(String message){
        log.info("发送消息："+message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }


}
