package com.z.core.net.channel;

import com.google.protobuf.AbstractMessageLite;
import com.z.common.util.SpringContext;
import com.z.core.service.game.game.RoomBizService;
import com.z.model.proto.MyMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

// 全局管理类
public class UserChannelManager {
    private static final Map<Long, Channel> channelMap = new ConcurrentHashMap<>();
    private static final Logger log = LogManager.getLogger(UserChannelManager.class);

    public static void bindUser(long userId, Channel channel) {
        channelMap.put(userId, channel);
    }

    public static Channel getChannel(long userId) {
        System.err.println("addChannel------->"+userId);
        return channelMap.get(userId);
    }
    //todo 离线后移除其他缓存
    public static void removeUser(long userId) {
        System.err.println("delChannel------->"+userId);
        channelMap.remove(userId);
        SpringContext.getBean(RoomBizService.class).out(userId);
    }
    public static Collection<Channel> getAllChannel() {
        return channelMap.values();
    }
//    public static boolean sendMsg(long uid,AbstractMessageLite resObj){
//        Channel channel = channelMap.get(uid);
//        if(channel == null) return false;
//        byte[] messageBytes = resObj.toByteArray();
//        channel.writeAndFlush(messageBytes);
//        return true;
//    }
    public static void  broad(AbstractMessageLite resObj) {
        byte[] messageBytes = resObj.toByteArray();
        // 创建 BinaryWebSocketFrame 并将字节数组写入其中
        BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageBytes));
        // 通过 WebSocket 发送消息
        for (Channel channel : channelMap.values()) {
            channel.writeAndFlush(resFrame);
        }
    }
    public static void  broad(AbstractMessageLite resObj, List<Long> idList) {
        if(idList == null || idList.isEmpty()) return;
        byte[] messageBytes = resObj.toByteArray();
        // 创建 BinaryWebSocketFrame 并将字节数组写入其中
        BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageBytes));
        for (Long id : idList) {
            Channel channel = channelMap.get(id);
            if(channel == null) continue;
            channel.writeAndFlush(resFrame);
        }
    }
    public static boolean  sendMsg(long uid, MyMessage.MyMsgRes res) {
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add(" msgId:"+res.getId());
        // 创建 BinaryWebSocketFrame 并将字节数组写入其中

        Channel channel = channelMap.get(uid);
        if(channel == null) return false;
        byte[] messageBytes = res.toByteArray();
        BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageBytes));
        channel.writeAndFlush(resFrame);
        log.info(sj.add("sucess").toString());
        return true;

    }
}
