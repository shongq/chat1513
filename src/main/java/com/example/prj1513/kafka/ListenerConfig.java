package com.example.prj1513.kafka;

import com.example.prj1513.model.ChatMessage;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static com.example.prj1513.kafka.KafkaConstants.*;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@EnableKafka
@Configuration
public class ListenerConfig {

    JsonDeserializer<ChatMessage> deserializer = new JsonDeserializer<>(ChatMessage.class);

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ChatMessage> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, ChatMessage> factory
                                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public Map<String, Object> consumerConfigurations(){
        setDeserializer();
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        configurations.put(GROUP_ID_CONFIG, GROUP_ID);
        configurations.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        configurations.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configurations.put(VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        return configurations;
    }

    @Bean
    public ConsumerFactory<String, ChatMessage> consumerFactory(){
        setDeserializer();
        return new DefaultKafkaConsumerFactory<>(consumerConfigurations(),
                new StringDeserializer(), deserializer);
    }

    private void setDeserializer() {
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
    }
}
