package com.z.core.net.handler;

import cn.hutool.json.JSONUtil;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.channel.UserChannelManager;
import com.z.core.schedule.ScheduledManager;
import com.z.core.util.SpringContext;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.StringJoiner;

//@Log4j2
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
//    protected Logger log = LoggerFactory.getLogger(getClass());
    private static final Log log = LogFactory.getLog(WebSocketFrameHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame){
        try {
            if (frame instanceof PingWebSocketFrame) {
                // 收到 Ping 帧，回应 Pong 帧
                PongWebSocketFrame pong = new PongWebSocketFrame(frame.content().retain());
                ctx.writeAndFlush(pong);
            } else if (frame instanceof PongWebSocketFrame) {
                // 处理 Pong 帧
               log.info("Received Pong from client");
            } else {
                // 处理其他 WebSocket 帧（例如文本或二进制数据）
//                super.channelRead(ctx, frame);
                read(ctx,frame);
            }
        } catch (Exception e) {
            log.error("",e);
        }
    }

    protected void read(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // 获取客户端的IP地址
        String clientIp = getClientIp(ctx);
        StringJoiner sj = new StringJoiner(",").add("ip:"+clientIp).add("frame:"+frame.getClass());
        log.info(sj.toString());

        if (frame instanceof TextWebSocketFrame) {
            log.info(sj.add("type:text").toString());
            // 处理文本帧
            String request = ((TextWebSocketFrame) frame).text();
            log.debug(sj.add("rec: " + request).toString());
            ctx.channel().writeAndFlush(new TextWebSocketFrame("Hello from server!"));
            log.debug(sj.add("send").toString());
        } else if (frame instanceof BinaryWebSocketFrame) {

            log.debug(sj.add("type:binary").toString());
            // 处理二进制帧（Protobuf）
            MyMessage.MyMsgReq message = MyMessage.MyMsgReq.parseFrom(frame.content().nioBuffer());
            log.info(sj.add("msgId:"+message.getId()).add("rec:"+message).toString());
            ProtocolDispatcher dispatcher = SpringContext.getBean(ProtocolDispatcher.class);

            // 心跳协议处理
            if (message.getId() == 1) { // 假设 10000 为心跳消息的 ID
                log.info(sj.add("heartbeat received").toString());
                sendHeartbeat(ctx);
                log.info(sj.add("heartbeat response sent").toString());
                return;
            }
//            logReq(message);
            AbstractMessageLite res = dispatcher.dispatch(ctx,message);
            // 响应 Protobuf 消息
            if(res == null){
                log.error(sj.add("handler null").toString());
                return;
            }
//            logRes(res);
            Long uid =  ctx.channel().attr(ChannelAttributes.USER_ID).get();
            log.debug(sj.add("uid:"+uid).add("res:"+res).toString());
            // 将 MyMessage 转换为字节数组
            byte[] messageBytes = res.toByteArray();
            // 创建 BinaryWebSocketFrame 并将字节数组写入其中
            BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageBytes));
            // 通过 WebSocket 发送消息
            ctx.channel().writeAndFlush(resFrame);
            log.debug(sj.add("uid:"+uid).add("res:"+res).add("sucess").toString());
        }
    }

    public void logReq(MyMessage.MyMsgReq message) {
        try {
            // 根据消息 ID 获取对应的 Protobuf 类
            Class<?> clazz = MsgManger.ins.get(message.getId());
            if (clazz == null) {
                log.error("No class found for message ID: " + message.getId());
                return;
            }

            // 合并 ByteString 列表为字节数组
            List<ByteString> list = message.getMsgList();
            if (list == null || list.isEmpty()) {
                log.error("Message list is empty for message ID: " + message.getId());
                return;
            }
            byte[] combinedBytes = ByteString.copyFrom(list).toByteArray();

            // 使用反射调用 `parseFrom` 方法解析消息
            Method parseMethod = clazz.getMethod("parseFrom", byte[].class);
            Object parsedMessage = parseMethod.invoke(null, combinedBytes);

            // 打印解析结果
            log.debug("Parsed request for message ID: " + message.getId() + " -> " + JSONUtil.parse(parsedMessage));
        } catch (Exception e) {
            // 捕获并打印异常
            log.error("Error processing message with ID: " + message.getId(), e);
        }
    }
    public void logRes(AbstractMessageLite res) {
        MyMessage.MyMsgRes message = (MyMessage.MyMsgRes)res;
        try {
            // 根据消息 ID 获取对应的 Protobuf 类
            Class<?> clazz = MsgManger.ins.get(message.getId());
            if (clazz == null) {
                log.error("No class found for message ID: " + message.getId());
                return;
            }

            // 合并 ByteString 列表为字节数组
            List<ByteString> list = message.getMsgList();
            if (list == null || list.isEmpty()) {
                log.error("Message list is empty for message ID: " + message.getId());
                return;
            }
            byte[] combinedBytes = ByteString.copyFrom(list).toByteArray();

            // 使用反射调用 `parseFrom` 方法解析消息
            Method parseMethod = clazz.getMethod("parseFrom", byte[].class);
            Object parsedMessage = parseMethod.invoke(null, JSONUtil.parse(combinedBytes));

            // 打印解析结果
            log.debug("Parsed res for message ID: " + message.getId() + " -> " + parsedMessage);
        } catch (Exception e) {
            // 捕获并打印异常
            log.error("Error processing message with ID: " + message.getId(), e);
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        if (userId != null) {
            UserChannelManager.removeUser(userId);
        }
        super.channelInactive(ctx);
        log.info("remove:"+userId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();

        ctx.close();
        log.error("exceptionCaught:"+cause.getMessage());
        log.error("==>",cause);
        log.error("uid:"+ ctx.channel().attr(ChannelAttributes.USER_ID).get());
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    log.warn("Reader idle detected, no data received.");
                    ctx.close(); // 超时关闭连接
                    break;
                case WRITER_IDLE:
                    log.warn("Writer idle detected, no data sent.");
                    sendHeartbeat(ctx);
                    break;
                case ALL_IDLE:
                    log.warn("All idle detected.");
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    private void sendHeartbeat(ChannelHandlerContext ctx) {

        // 构造响应帧
        // 发送心跳响应
        MyMessage.MyMsgRes heartbeatResponse = MyMessage.MyMsgRes.newBuilder()
                .setId(2).setOk(true)
                .setHeart(MyMessage.Heartbeat.newBuilder()
                        .setTimestamp(System.currentTimeMillis()).setMessage("ack").build()).build();

        byte[] messageBytes = heartbeatResponse.toByteArray();
        BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageBytes));
        ctx.channel().writeAndFlush(resFrame);
        log.info("Heartbeat request sent.");
    }

    //    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state() == IdleState.READER_IDLE) {
//                // 如果连接空闲超过设定时间，触发心跳机制
//                log.info("Sending Ping to client");
//                PingWebSocketFrame ping = new PingWebSocketFrame();
//                ctx.writeAndFlush(ping);
//            }
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }
//    }
    private String getClientIp(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getAddress().getHostAddress();
    }
}
