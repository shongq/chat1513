package com.example.prj1513.service;

import com.example.prj1513.kafka.KafkaConstants;
import com.example.prj1513.model.ChatMessage;
import com.example.prj1513.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> reidsTemplate;
    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    private final ChatRoomRepository chatRoomRepository;

    public String getRoomId(String destination){
        int lastIndex = destination.lastIndexOf("/");
        if(lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    //Redis를 통해 Topic에 메세지 전송
    public void sendRedisChatMessage(ChatMessage chatMessage){
        makeChatMessage(chatMessage);
        reidsTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    //Kafka를 통해 Topic에 메세지 전송
    public void sendKafkaChatMessage(ChatMessage chatMessage){
        makeChatMessage(chatMessage);
        Message<ChatMessage> message = MessageBuilder
                .withPayload(chatMessage)
                .setHeader(KafkaHeaders.TOPIC, KafkaConstants.KAFKA_TOPIC)
                .build();
        kafkaTemplate.send(message);
    }

    private void makeChatMessage(ChatMessage chatMessage) {
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
}
