package com.z.core.net.handler;

import cn.hutool.json.JSONUtil;
import com.google.protobuf.*;
import com.z.common.util.SpringContext;
import com.z.core.net.channel.ChannelAttributes;
import com.z.core.net.channel.UserChannelManager;
import com.z.model.proto.Game;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.StringJoiner;

@Log4j2
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame){
        try {
            read(ctx,frame);
        } catch (Exception e) {
            log.error("",e);
        }
    }

    protected void read(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // 获取客户端的IP地址
        String clientIp = getClientIp(ctx);
        StringJoiner sj = new StringJoiner(",").add("ip:"+clientIp).add("frame:"+frame.getClass());
        log.debug(sj.toString());
        if (frame instanceof TextWebSocketFrame) {
            log.debug(sj.add("type:text").toString());
            // 处理文本帧
            String request = ((TextWebSocketFrame) frame).text();
            log.debug(sj.add("rec: " + request).toString());
            ctx.channel().writeAndFlush(new TextWebSocketFrame("Hello from server!"));
            log.debug(sj.add("send").toString());
        } else if (frame instanceof BinaryWebSocketFrame) {
            log.debug(sj.add("type:binary").toString());
            // 处理二进制帧（Protobuf）
            MyMessage.MyMsgReq message = MyMessage.MyMsgReq.parseFrom(frame.content().nioBuffer());
            log.debug(sj.add("msgId:"+message.getId()).add("rec:"+message).toString());
            ProtocolDispatcher dispatcher = SpringContext.getBean(ProtocolDispatcher.class);

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
    }

    private String getClientIp(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getAddress().getHostAddress();
    }

    public static void main(String[] args) {

        //
        User.C_10001 req =  User.C_10001.newBuilder().setPhone("1232321").setDeviceId("adasfsdfas").build();

        MyMessage.MyMsgReq.Builder builder =  MyMessage.MyMsgReq.newBuilder();
        builder.addMsg(ByteString.copyFrom(req.toByteArray()));
        MyMessage.MyMsgReq msgReq = builder.setId(10001).build();

        byte[] bytes = msgReq.toByteArray();
//        bytes =new byte[]{8, 1, 16, -111, 78, 26, 60, 10, 11, 49, 55, 51, 51, 49, 55, 56, 48, 56, 56, 48, 18, 3, 49, 50, 51, 26, 40, 55, 57, 102, 48, 56, 50, 99, 50, 51, 100, 57, 55, 102, 98, 100, 56, 49, 50, 100, 52, 48, 99, 97, 49, 49, 55, 53, 50, 50, 51, 54, 51, 54, 51, 100, 98, 57, 99, 52, 51, 0, 0, 0, 0};

        try {
            MyMessage.MyMsgReq myMessage =  MyMessage.MyMsgReq.parseFrom(bytes);
            List<ByteString> list = myMessage.getMsgList();
            User.C_10001  res =  User.C_10001.parseFrom(ByteString.copyFrom(list).toByteArray());
            log.info("反序列化成功:{}, {}",myMessage,res);
        } catch (InvalidProtocolBufferException e) {
            log.error("反序列化失败: {}", e.getMessage());
        }

    }

}
