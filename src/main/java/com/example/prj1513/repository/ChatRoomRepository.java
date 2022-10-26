package com.example.prj1513.repository;

import com.example.prj1513.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {

    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private static final String USER_COUNT = "USER_COUNT";
    private static final String ENTER_INFO = "ENTER_INFO";

    private final RedisTemplate<String, Object> redisTemplate;

    HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    HashOperations<String, String, String> hashOpsEnterInfo;
    ValueOperations<String, Object> valueOps;

    @PostConstruct
    private void init(){
        hashOpsChatRoom = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();
        valueOps = redisTemplate.opsForValue();
    }

    public List<ChatRoom> findAllRoom(){
        List<ChatRoom> chatRooms = hashOpsChatRoom.values(CHAT_ROOMS);
        return chatRooms;
    }

    public ChatRoom findRoomById(String id){
        return hashOpsChatRoom.get(CHAT_ROOMS, id);
    }

    public ChatRoom createChatRoom(String name){
        ChatRoom chatRoom = ChatRoom.create(name);
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public void setUserEnterInfo(String sessionId, String roomId){
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    public String getUserEnterRoomId(String sessionId){
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    public void removeUserEnterInfo(String sessionId){
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    public long getUserCount(String roomId){
        return Long.valueOf((String) Optional.ofNullable(valueOps.get(USER_COUNT+"_"+roomId)).orElse("0"));
    }

    public long plusUserCount(String roomId){
        return Optional.ofNullable(valueOps.increment(USER_COUNT+"_"+roomId)).orElse(0L);
    }

    public long minusUserCount(String roomId){
        return Optional.ofNullable(valueOps.decrement(USER_COUNT+"_"+roomId)).orElse(0L);
    }
}
