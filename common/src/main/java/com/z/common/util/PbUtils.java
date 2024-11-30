package com.z.common.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class PbUtils {
    protected Logger log = LoggerFactory.getLogger(getClass());
    public  String pbToJson(MessageOrBuilder message){
        try {
            return JsonFormat.printer().includingDefaultValueFields().print(message);
        } catch (InvalidProtocolBufferException e) {
            log.error("pbToJson:"+message,e);
        }

        return Strings.EMPTY;
    }
}
