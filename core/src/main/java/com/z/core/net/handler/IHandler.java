package com.z.core.net.handler;

import com.google.protobuf.AbstractMessageLite;
import com.z.model.proto.MyMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * 消息处理接口
 *
 */
public interface IHandler<T>{

    int getMsgId();

    AbstractMessageLite handle(ChannelHandlerContext ctx, MyMessage.MyMsgReq msgReq) throws Exception;
    /**
     * 处理消息逻辑
     */
    AbstractMessageLite handleDo(ChannelHandlerContext ctx, T req);

}
