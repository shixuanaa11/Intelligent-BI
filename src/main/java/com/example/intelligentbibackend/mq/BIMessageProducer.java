package com.example.intelligentbibackend.mq;

import com.example.intelligentbibackend.constant.BiMqConstant;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


/**
 * RabbitMQ消息生产者
 * 交换机的名称，消息将被发送到这个交换机
 * 路由键，用于指定消息应该发送到哪个队列
 * 要发送的消息内容
 */

@Component
public class BIMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {

   rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, message);
    }
}
