package com.rabbitmq.manager.rabbitmq_jieun;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.manager.netty_yumi.MessageHandler;
import com.rabbitmq.manager.vo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
public class ListenerService {

    @Autowired
    private MessageHandler messageHandler;


    private ObjectMapper objectMapper = new ObjectMapper();

    public void receive(String in, String receiver) throws
            InterruptedException, JsonMappingException, JsonProcessingException {
        StopWatch watch = new StopWatch();
        watch.start();
        System.out.println(in.getClass());
        Message message = objectMapper.readValue(in, Message.class);
        System.out.println("instance " + receiver + " [x] Received '"
                + message.getId() + "'");
        doWork(message.getAfterType());
        watch.stop();
        //System.out.println("instance " + receiver + " [x] Done in "
        //        + watch.getTotalTimeSeconds() + "s");
        messageHandler.request(message);
    }
    private void doWork(String in) throws InterruptedException {
        for (char ch : in.toCharArray()) {
            if (ch == '.') {
                Thread.sleep(1000);
            }
        }
    }
}
