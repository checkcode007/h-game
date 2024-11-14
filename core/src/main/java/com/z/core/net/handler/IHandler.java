package com.z.core.net.handler;

import com.google.protobuf.AbstractMessageLite;

/**
 * 消息处理接口
 *
 */
public interface IHandler {
    AbstractMessageLite handle(MsgData msgData);
}
