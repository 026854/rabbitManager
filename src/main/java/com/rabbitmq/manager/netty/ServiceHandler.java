package com.rabbitmq.manager.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ChannelHandler.Sharable
public class ServiceHandler  extends ChannelInboundHandlerAdapter {
    //입력된 데이터를 처리하는 이벤트 핸들러 상속

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    final AttributeKey<Integer> status = AttributeKey.newInstance("status");
    final AttributeKey<String> clinetID = AttributeKey.newInstance("clientID");
    private static final AtomicInteger count = new AtomicInteger(0);
    @Autowired
    ChannelGroup channelList;

    @Autowired
    Map<String, Channel> channelMap;

    //채널 활성화 ; 최초에 채널 연결할 때 실행
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //int value = count.incrementAndGet();
        //ctx.channel().attr(id).set(value);
        //logger.info("cxt.channel.id ="+ctx.channel().id().asLongText());
        logger.info("cxt.channel.id ="+ctx.channel().id().asShortText());
        //channels.add(ctx.channel());
        ctx.channel().attr(status).set(0);
        channelList.add(ctx.channel());
    }

    @Override
    //데이터 수신 이벤트 처리 메서드. 클라이언트로부터 데이터 수신이 이뤄졌을 때 실행
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 변환기 처리 결과.. 받아야 하는 부분?
        ByteBuf byteBuf = (ByteBuf) msg;
        logger.debug("message : {} ",byteBuf.toString(Charset.defaultCharset()));
        ctx.channel().attr(status).set(0);

        //채널 파이프라인에 대한 이벤트 처리 writeAndFlush 데이터를 쓰고 버퍼를 전송
        //channels.writeAndFlush(msg);
        //channels.flush();
        /*
        Channel clientChannel = ctx.channel();
        for(Channel channel : channels){
            if(channel.id() == ctx.channel().id()){
                channel.writeAndFlush(msg);
            }
        }*/
    }
}
