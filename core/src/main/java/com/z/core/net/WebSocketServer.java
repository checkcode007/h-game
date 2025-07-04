package com.z.core.net;

import com.z.core.net.handler.WebSocketFrameHandler;
import com.z.model.proto.MyMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

//@Slf4j
@Component
public class WebSocketServer implements ApplicationListener<ApplicationReadyEvent> {
private static final Log logger = LogFactory.getLog(WebSocketServer.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event)  {
        try {
            logger.info("-----webstart-----");
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
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 设置连接超时为 10 秒
                    .option(ChannelOption.TCP_NODELAY, true) // 禁用 Nagle 算法，提升小数据包传输效率
                    .option(ChannelOption.SO_REUSEADDR, true) // 允许重用地址
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 开启 TCP 长连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel( SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            pipeline.addLast(new WebSocketFrameAggregator(65536)); // 添加帧聚合器
                            pipeline.addLast(new ProtobufDecoder(MyMessage.MyMsgReq.getDefaultInstance()));
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new IdleStateHandler(30,15,0, TimeUnit.SECONDS));
                            pipeline.addLast(new WebSocketFrameHandler());
                            pipeline.addLast(new SimpleChannelInboundHandler<BinaryWebSocketFrame>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
                                    // 在这里处理 Socket 消息
                                    MyMessage.MyMsgReq message =MyMessage.MyMsgReq.parseFrom(msg.content().nioBuffer());
                                    logger.error("fail msg:"+message);
                                }
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    logger.error("发生异常", cause);
                                    ctx.close();
                                }
                            });
                        }
                    });
            // 添加 Shutdown Hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("正在关闭 WebSocket 服务器...");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                logger.info("WebSocket 服务器已关闭");
            }));
//             b.bind(port).sync();
            Channel ch = b.bind(port).sync().channel();
            logger.info("WebSocket服务器在端口=======> " + port + " 启动");

            // 使用addListener()来处理通道关闭事件
            ch.closeFuture().addListener((ChannelFuture future) -> {
                logger.info("WebSocket服务器在端口=======> 结束 " + port);
                // 在这里可以添加其他关闭后的逻辑
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });

            //  在这里可以继续处理其他逻辑，而不会被阻塞
            // 比如：等待某个条件后再关闭服务器
            ch.closeFuture().sync();//阻塞
        } catch (Error e) {
            logger.error(" websocket---->",e);
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
