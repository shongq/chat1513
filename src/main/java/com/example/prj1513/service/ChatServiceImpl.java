package com.example.prj1513.service;

import com.example.prj1513.common.interfaces.ChatService;
import com.example.prj1513.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ChatService {
    private final KafkaChatService kafkaChatService;
    private final RedisChatService redisChatService;

    @Override
    public void sendMessage(ChatMessage chatMessage, boolean isRedis) {
        if(isRedis) {
            redisChatService.pubMessage(chatMessage);
        } else {
            kafkaChatService.pubMessage(chatMessage);
        }
    }
}
