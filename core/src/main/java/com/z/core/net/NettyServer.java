package com.z.core.net;

import com.z.core.net.handler.MyMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.logging.Logger;

//@Component
public class NettyServer implements ApplicationListener<ApplicationReadyEvent> {
    Logger logger = Logger.getLogger(NettyServer.class.getName());
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event)  {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new MyMessageHandler());
                        }
                    });

            logger.info("netty start=====>1");
            ChannelFuture f = b.bind(8080).sync();
            logger.info("netty start=====>2");
            f.channel().closeFuture().sync();
            logger.info("netty start=====>3");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
