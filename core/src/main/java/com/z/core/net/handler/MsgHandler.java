package com.z.core.net.handler;

import com.z.core.net.handler.proto.user.UserReq10001;
import com.z.core.net.handler.proto.user.UserReq10003;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsgHandler {
    @Autowired
    private UserReq10001 req10001;

    @Autowired
    private UserReq10003 req10003;

    private Map<Integer, IHandler> map = new HashMap<>();
    public void initMsg(){
        map.put(10001, req10001);
        map.put(10003, req10003);

    }
    public IHandler get(int key) {
        return map.get(key);
    }
}
