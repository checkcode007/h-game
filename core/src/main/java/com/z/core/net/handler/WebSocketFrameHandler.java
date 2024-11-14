package com.z.core.net.handler;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.z.model.proto.MyMessageOuterClass;
import com.z.model.type.MsgEnum;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Log4j2
@Service
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Autowired
    private MsgHandler msgHandler;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws InvalidProtocolBufferException {
        // 获取客户端的IP地址
        String clientIp = getClientIp(ctx);
        log.info("Client IP: " + clientIp);
        if (frame instanceof TextWebSocketFrame) {
            // 处理文本帧
            String request = ((TextWebSocketFrame) frame).text();
            log.info("Received: " + request);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("Hello from server!"));
        } else if (frame instanceof BinaryWebSocketFrame) {
            // 处理二进制帧（Protobuf）
            MyMessageOuterClass.MyMessage message = MyMessageOuterClass.MyMessage.parseFrom(frame.content().nioBuffer());
            log.info("Received Protobuf message: " + message);
            // 响应 Protobuf 消息
            int msgCode = message.getId();
            IHandler handler = msgHandler.get(msgCode);
            if(handler == null){
                log.error("handler fail null msgCode:"+msgCode);
                return;
            }
            MsgData msgData = new MsgData();
            if(message.hasUserMsg()){
                msgData.setType(MsgEnum.MsgType.USR);
                msgData.setUserMsg(message.getUserMsg());
            }else if (message.hasGameMsg()){
                msgData.setType(MsgEnum.MsgType.GAME);
                msgData.setGameMsg(message.getGameMsg());
            }else{
                log.error("msg ext no handler=======>msgCode:"+msgCode);
                return;
            }
            AbstractMessageLite resObj = handler.handle(msgData);
            log.info(" res======>"+resObj);
            // 将 MyMessage 转换为字节数组
            byte[] messageBytes = resObj.toByteArray();
            // 创建 BinaryWebSocketFrame 并将字节数组写入其中
            BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageBytes));
            // 通过 WebSocket 发送消息
            ctx.channel().writeAndFlush(resFrame);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private String getClientIp(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getAddress().getHostAddress();
    }
}
