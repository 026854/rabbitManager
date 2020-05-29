package com.rabbitmq.manager.config;

import com.rabbitmq.manager.rabbitmq_jieun.Manager;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration

public class RabbitConfiguration {
	private static final String HIGH_QUEUE_NAME = "high-queue";
	private static final String NORMAL_QUEUE_NAME = "normal-queue";
	private static final String DIRECT_EXCHANGE_NAME = "direct-exchange";

	@Bean
	public Manager manager() {
		return new Manager();
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange(DIRECT_EXCHANGE_NAME);
	}

	@Bean
	public Queue highQueue() {
		return new Queue(HIGH_QUEUE_NAME);
		// durable 브로커가 재시작 할 때 남아있는지 여부
	}

	@Bean
	public Queue normalQueue() {
		return new Queue(NORMAL_QUEUE_NAME);
	}

	@Bean
	public Binding bindingWithHighQueue(Queue highQueue, DirectExchange exchange) {
		return BindingBuilder.bind(highQueue()).to(exchange).with("high");
	}

	@Bean
	Binding bindingWithNormalQueue(Queue normalQueue, DirectExchange exchange) {
		return BindingBuilder.bind(normalQueue()).to(exchange).with("normal");
	}

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public Map<String, Channel> channelMap(){
		return new HashMap<>();
	}

	@Bean
	public ChannelGroup ChannelList(){
		return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}

	@Bean
	public AmqpAdmin amqpAdmin(RabbitTemplate template){
		return new RabbitAdmin(template);
	}
}
