package com.example.prj1513.redis;

import com.example.prj1513.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * redis에서 메세지가 pub되면 대기하고 있다가 메세지를 받아 처리한다.
     * @param publishMessage
     */
    public void listen(String publishMessage) {
        try{
            //ChatMessage 객체로 매핑
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            //채팅방을 구독한 사용자들에게 메세지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
        } catch(Exception e){
            log.error(e.getMessage());
        }
    }
}
