package com.example.prj1513.common.utils;

import com.example.prj1513.model.ChatMessage;
import com.example.prj1513.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatUtils {

    private final ChatRoomRepository chatRoomRepository;

    public void makeChatMessage(ChatMessage chatMessage) {
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        if(ChatMessage.MessageType.ENTER.equals(chatMessage.getType())){
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        }else if(ChatMessage.MessageType.QUIT.equals(chatMessage.getType())){
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }
        log.info("user : " + chatMessage.getSender() + " / " + " message : " + chatMessage.getMessage());
    }

    public String getRoomId(String destination){
        int lastIndex = destination.lastIndexOf("/");
        if(lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }
}
