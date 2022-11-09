package com.example.prj1513.service;

import com.example.prj1513.common.interfaces.ChatService;
import com.example.prj1513.common.utils.ChatUtils;
import com.example.prj1513.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisChatService extends ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> reidsTemplate;
    private final ChatUtils chatUtils;

    @Override
    public void pubMessage(ChatMessage chatMessage) {
        chatUtils.makeChatMessage(chatMessage);
        reidsTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
