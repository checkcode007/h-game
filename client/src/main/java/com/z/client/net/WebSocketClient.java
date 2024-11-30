package com.z.client.net;

import com.z.model.proto.MyMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.net.URI;

public class WebSocketClient {

    private final String host;
    private final int port;

    public WebSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        URI uri = URI.create("ws://" + host + ":" + port+"/ws");

        // 创建 WebSocket 握手器
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V13, null, false, HttpHeaders.EMPTY_HEADERS, 128);

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new HttpResponseDecoder(),  // 解码 HTTP 响应
                                    new HttpRequestEncoder(),   // 编码 HTTP 请求
                                    new WebSocketClientProtocolHandler(handshaker),
                                    new WebSocketFrameAggregator(1024),
                                    new ProtobufDecoder(MyMessage.MyMsgRes.getDefaultInstance()), // Proto3 解码器
                                    new ProtobufEncoder(), // Proto3 编码器
                                    new WebSocketClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new WebSocketClient("localhost", 8081).start();
    }
}
