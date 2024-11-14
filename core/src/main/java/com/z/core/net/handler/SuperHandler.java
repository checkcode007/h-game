package com.z.core.net.handler;

import com.google.protobuf.AbstractMessageLite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SuperHandler implements IHandler {
    private static final Logger log = LogManager.getLogger(SuperHandler.class);

    @Override
    public AbstractMessageLite handle(MsgData msgData) {
        log.info("receive------------>"+msgData);
        return null;
    }
}
