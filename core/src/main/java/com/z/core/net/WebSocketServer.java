package com.z.core.net;

import com.z.core.net.handler.WebSocketFrameHandler;
import com.z.model.proto.MyMessageOuterClass;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class WebSocketServer implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private WebSocketFrameHandler webSocketFrameHandler;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event)  {
        try {
            log.info("-----webstart-----");
            start(8081);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel( SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            pipeline.addLast(new ProtobufDecoder(MyMessageOuterClass.MyMessage.getDefaultInstance()));
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(webSocketFrameHandler);
                        }
                    });
//             b.bind(port).sync();
            Channel ch = b.bind(port).sync().channel();
            log.info("WebSocket服务器在端口=======> " + port + " 启动");

            // 使用addListener()来处理通道关闭事件
            ch.closeFuture().addListener((ChannelFuture future) -> {
                log.info("WebSocket服务器在端口=======> 结束 " + port);
                // 在这里可以添加其他关闭后的逻辑
            });

            //  在这里可以继续处理其他逻辑，而不会被阻塞
            // 比如：等待某个条件后再关闭服务器

            ch.closeFuture().sync();//阻塞


        } catch (Error e) {
            log.error(" websocket---->",e);
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
