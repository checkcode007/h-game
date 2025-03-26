package com.z.core.net.channel;

import com.google.protobuf.AbstractMessageLite;
import com.z.model.type.RedisKey;
import com.z.core.service.user.UserBizService;
import com.z.core.service.user.UserService;
import com.z.core.util.RedisUtil;
import com.z.core.util.SpringContext;
import com.z.model.bo.user.User;
import com.z.model.proto.CommonGame;
import com.z.model.proto.MyMessage;
import com.z.model.type.GameName;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

// 全局管理类
public class UserChannelManager implements ApplicationListener<ApplicationReadyEvent> {
    private static final Map<Long, Channel> channelMap = new ConcurrentHashMap<>();
//    private static final Logger log = LogManager.getLogger(UserChannelManager.class);
    private static final Log logger = LogFactory.getLog(UserChannelManager.class);

    public static void bindUser(long userId, Channel channel) {
        channelMap.put(userId, channel);
        RedisUtil.sadd(RedisKey.ONLINE_USERS,userId);
        logger.info("addChannel------->"+userId);
    }

    public static Channel getChannel(long userId) {

        return channelMap.get(userId);
    }
    //todo 离线后移除其他缓存
    public static void removeUser(long userId) {
        logger.info("delChannel------->"+userId);
        channelMap.remove(userId);
        SpringContext.getBean(UserBizService.class).logout(userId);
        RedisUtil.setRemove(RedisKey.ONLINE_USERS,userId);
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

    public static boolean  sendMsg(long uid, MyMessage.MyMsgRes res) {
        StringJoiner sj = new StringJoiner(",").add("uid:"+uid).add(" msgId:"+res.getId());
        // 创建 BinaryWebSocketFrame 并将字节数组写入其中

        Channel channel = channelMap.get(uid);
        if(channel == null) return false;
        byte[] messageBytes = res.toByteArray();
        BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageBytes));
        channel.writeAndFlush(resFrame);
        logger.info(sj.add("sucess").toString());
        return true;

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
    public static void  broadReward(CommonGame.GameType gameType,long uid,long gold) {
        if(gold < 10000) return;
        if(channelMap == null || channelMap.isEmpty()) return;
        User user = UserService.ins.get(uid);
        if(user == null) return;
        String msg = "玩家:"+user.getUser().getName()+"在游戏"+ GameName.getType(gameType).getName()+"获得金币:"+gold;
        for (Channel channel : channelMap.values()) {
            AbstractMessageLite resObj = MyMessage.Broadcast.newBuilder()
                    .setType(MyMessage.BroadType.BT_DEFAULT).setMsg(msg)
                    .build();
            byte[] messageBytes = resObj.toByteArray();
            // 创建 BinaryWebSocketFrame 并将字节数组写入其中
            BinaryWebSocketFrame resFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(messageBytes));
            channel.writeAndFlush(resFrame);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        RedisUtil.del(RedisKey.ONLINE_USERS);
    }
}
