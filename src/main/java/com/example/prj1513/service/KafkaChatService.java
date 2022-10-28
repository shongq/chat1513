package com.example.prj1513.service;

import com.example.prj1513.common.interfaces.ChatService;
import com.example.prj1513.common.utils.ChatUtils;
import com.example.prj1513.kafka.KafkaConstants;
import com.example.prj1513.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaChatService implements ChatService {

    private final ChatUtils chatUtils;
    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    //Kafka를 통해 Topic에 메세지 전송
    public void sendMessage(ChatMessage chatMessage){
        chatUtils.makeChatMessage(chatMessage);
        Message<ChatMessage> message = MessageBuilder
                .withPayload(chatMessage)
                .setHeader(KafkaHeaders.TOPIC, KafkaConstants.KAFKA_TOPIC)
                .build();
        kafkaTemplate.send(message);
    }
}
