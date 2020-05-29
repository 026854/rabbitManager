package com.rabbitmq.manager.rabbitmq_jieun;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


public class Manager {
    private static final String HIGH_QUEUE_NAME = "high-queue";
    private static final String NORMAL_QUEUE_NAME = "normal-queue";
    private static final String DIRECT_EXCHANGE_NAME = "direct-exchange";
    
    @Autowired
    private RabbitTemplate template;


    @Autowired
    private ListenerService listenerService;

    @Autowired
    private AmqpAdmin amqpAdmin;

    //@RabbitHandler // 메세지 타입에 따라 메서드 매핑
    @RabbitListener(queues ="high-queue", concurrency = "3")
    public void highReceiver(String message) throws InterruptedException, JsonMappingException, JsonProcessingException {
    	//RabbitAdmin admin = new RabbitAdmin(template);
    	System.out.println("message: "+amqpAdmin.getQueueInfo("high-queue").getMessageCount());
        listenerService.receive(message, "high");
    }

    @RabbitListener(queues ="normal-queue")
    public void normalReceiver(String message) throws InterruptedException, JsonMappingException, JsonProcessingException {
        listenerService.receive(message, "normal");
    }


}
