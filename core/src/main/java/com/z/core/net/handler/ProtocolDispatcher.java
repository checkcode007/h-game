package com.z.core.net.handler;

import com.google.protobuf.AbstractMessageLite;
import com.z.model.proto.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Log4j2
@Service
public class ProtocolDispatcher {

    private final List<IHandler<?>> handlers; // 自动注入所有 ProtocolHandler
    private final Map<Integer, IHandler<?>> handlerMap = new HashMap<>();

    @Autowired
    public ProtocolDispatcher(List<IHandler<?>> handlers) {
        this.handlers = handlers;
    }

    /**
     * 初始化 handlerMap
     */
    @PostConstruct
    public void init() {
        for (IHandler<?> handler : handlers) {
            handlerMap.putIfAbsent(handler.getMsgId(), handler);
            log.info("id:"+handler.getMsgId()+" handler:"+handler.getClass().getName());
        }
    }
    /**
     * 分发消息
     */
    public AbstractMessageLite dispatch(ChannelHandlerContext ctx, MyMessage.MyMsgReq req) {
        IHandler<?> handler = handlerMap.get(req.getId());
        StringJoiner sj = new StringJoiner(",").add("id:"+req.getId());
        log.info(sj.toString());
        if (handler == null) {
            log.error(sj.add("handler null").toString());
            return null;
        }
        log.info(sj.add("handler:"+handler.getClass().getName()).toString());
        try {
            return handler.handle(ctx,req);
        } catch (Exception e) {
            log.error(sj.toString(),e);
        }
        return null;
    }
}
