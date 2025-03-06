package com.z.model.meilisearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MSIndex(uid = "gold_log", primaryKey = "id")
public class MSGoldLog {
    @MSFiled(openFilter = true, key = "id", openSort = true)
    String id;
    @MSFiled(openFilter = true, key = "type", openSort = true)
    int type;
    @MSFiled(openFilter = true, key = "addType", openSort = true)
    int addType;
    @MSFiled(openFilter = true, key = "roomId", openSort = true)
    long roomId;
    @MSFiled(openFilter = true, key = "gameId", openSort = true)
    long gameId;
    /**
     * @see com.z.model.proto.CommonGame.RoomType
     */
    @MSFiled(openFilter = true, key = "roomType", openSort = true)
    long roomType;
    /**
     * @see com.z.model.proto.CommonGame.GameType
     */
    @MSFiled(openFilter = true, key = "gameType", openSort = true)
    long gameType;
    @MSFiled(openFilter = true, key = "g", openSort = true)
    long g;
    @MSFiled(openFilter = true, key = "t", openSort = true)
    long t;
    @MSFiled(openFilter = true, key = "d", openSort = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date d;

}
