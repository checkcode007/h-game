package com.z.client.net;

import com.google.protobuf.ByteString;
import com.z.model.common.MsgId;
import com.z.model.proto.MyMessage;
import com.z.model.proto.User;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<MyMessage.MyMsgRes> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage.MyMsgRes msg) throws Exception {
        System.out.println("Received message: " + msg.getId());
        MyMessage.MyMsgReq.Builder b = MyMessage.MyMsgReq.newBuilder();
       if (msg.getId() == MsgId.S_LOGIN){

       }
    }

    public void sendMessage(ChannelHandlerContext ctx, String message, int id) {
        MyMessage.MyMsgReq myMessage = MyMessage.MyMsgReq.newBuilder().setId(id).build();
        ctx.writeAndFlush(myMessage);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // 发送初始消息
//        sendMessage(ctx, "Hello WebSocket Server", 1);

        User.C_10003 req =  User.C_10003.newBuilder().setPhone("3000000").setDeviceId("adasfsdfas").setPwd("3000000").build();

        MyMessage.MyMsgReq.Builder builder =  MyMessage.MyMsgReq.newBuilder().setId(10003);
        builder.addMsg(ByteString.copyFrom(req.toByteArray()));

        byte[] bytes = builder.build().toByteArray();
        BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes));
        ctx.writeAndFlush(resFrame);
    }
}
