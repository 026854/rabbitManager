package com.rabbitmq.manager.netty;

import com.rabbitmq.manager.vo.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageHandler {

    @Autowired
    ServiceHandler serviceHandler;
    @Autowired
    ChannelGroup channelList;
    Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Autowired
    Map<String, Channel> channelMap;

    public void request(Message msg){
        logger.info("msg : "+msg);
        ByteBuf messageBuffer = Unpooled.buffer();
        String m = msg.getId()+ " : " +msg.getPath();
        messageBuffer.writeBytes(m.getBytes());

        for( Channel channel : channelList){ //근데 다 1이면?
            if(channel.attr(serviceHandler.status).get() ==1) continue;
            else{

                channel.attr(serviceHandler.clinetID).set(msg.getId());
                channel.writeAndFlush(messageBuffer);
                break;
            }
        }

    }


}


