package com.example.prj1513.kafka;

import com.example.prj1513.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> reidsTemplate;

    @KafkaListener(
            topics = KafkaConstants.KAFKA_TOPIC,
            groupId = KafkaConstants.GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ChatMessage publishMessage){
        try{
//            reidsTemplate.convertAndSend(channelTopic.getTopic(), publishMessage);
            messagingTemplate.convertAndSend("/sub/chat/room/" + publishMessage.getRoomId(), publishMessage);
        } catch(Exception e){
            log.error(e.getMessage());
        }
    }
}
