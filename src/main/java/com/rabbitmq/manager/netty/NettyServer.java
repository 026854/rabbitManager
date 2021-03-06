package com.rabbitmq.manager.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:/application.properties")
public class NettyServer {

    @Value("${tcp.port}")
    private int tcpPort;
    @Value("${boss.thread.count}")
    private int bossCount;
    @Value("${worker.thread.count}")
    private int workerCount;

    /**
     * The constant SERVICE_HANDLER.
     */
    @Autowired private ServiceHandler serviceHandler;
    /**
     * Start.
     */
    public void start() {
        /**
         * 클라이언트 연결을 수락하는 부모 스레드 그룹
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossCount); //스레드 갯수

        /**
         * 연결된 클라이언트의 소켓으로 부터 데이터 입출력 및 이벤트를 담당하는 자식 스레드
         */
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //스레드 갯수 ;내부 설정에 의해 cpu 코어 수에 따라 설정

        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//서버 소켓 입출력 모드를 NIO로 설정
                    .handler(new LoggingHandler(LogLevel.INFO))//서버 소켓 채널의 이벤트 핸들러 등록
                    .childHandler(new ChannelInitializer<SocketChannel>() { //클라이언트 소켓 채널로 송수신 되는 데이터 가공 핸들러
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            //핸들러 추가
                            pipeline.addLast(serviceHandler);
                        }
                    });
            //ChannelFuture : 비동기 방식의 작업 처리 후 결과를 제어
            //클라이언트의 요청이 들어올 수 있게 포트를 활성화 시킴
            ChannelFuture channelFuture = b.bind(tcpPort).sync();
            //서버 소켓이 닫힐 때까지 대기
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
