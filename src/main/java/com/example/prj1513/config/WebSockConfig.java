package com.example.prj1513.config;

import com.example.prj1513.config.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    /**
     * Stomp를 사용해 sub/pub 메세징 구현하기 위한 prefix 설정
     * @param config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/sub");  //메세지 수신 요청의 prefix
        config.setApplicationDestinationPrefixes("/pub");   //메세지 전송 요청의 prefix
    }

    /**
     * Websocket endpoint 설정 : /ws-stomp
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * WebSocket 요청 처리 전 interceptor 처리
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(stompHandler);
    }


    /**
     * Websocket endpoint : /ws/chat1513
     * CORS setAllowedOrigins : 다른 서버에서도 접속 가능하도록 설정
     */
    /*@Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/chat1513").setAllowedOrigins("*");
    }*/
}
