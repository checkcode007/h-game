package com.z.core.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyMessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        // 处理 ByteBuf 数据
        // 将 ByteBuf 转换为字符串（假设编码为 UTF-8）
        String receivedMessage = msg.toString(io.netty.util.CharsetUtil.UTF_8);
        System.out.println("Received message: " + receivedMessage);
    }
}
