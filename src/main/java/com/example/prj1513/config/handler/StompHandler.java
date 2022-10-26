package com.example.prj1513.config.handler;

import com.example.prj1513.model.ChatMessage;
import com.example.prj1513.repository.ChatRoomRepository;
import com.example.prj1513.service.ChatService;
import com.example.prj1513.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(StompCommand.CONNECT==accessor.getCommand()){    //채팅방 입장한 경우
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);
            jwtTokenProvider.validateToken(jwtToken);   //token 유효성 검증
        } else if(StompCommand.SUBSCRIBE==accessor.getCommand()){   //채팅방에 대해 구독하는 경우
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination"))
                                                .orElse("InvalidRoomId"));
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser"))
                    .map(Principal::getName).orElse("UnknownUser");

            chatRoomRepository.setUserEnterInfo(sessionId, roomId); //사용자 session 등록
            chatRoomRepository.plusUserCount(roomId);   //UserCount +1
            chatService.sendKafkaChatMessage(ChatMessage.builder()  //메세지 전송
                                        .type(ChatMessage.MessageType.ENTER)
                                        .roomId(roomId)
                                        .sender(name)
                                        .build());
            log.info("SUBSCRIBED {}, {}", name, roomId);
        } else if(StompCommand.DISCONNECT==accessor.getCommand()){  //채팅방 연결 종료인 경우
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser"))
                    .map(Principal::getName).orElse("UnknownUser");
            chatRoomRepository.minusUserCount(roomId);   //UserCount -1
            chatService.sendKafkaChatMessage(ChatMessage.builder()  //메세지 전송
                            .type(ChatMessage.MessageType.QUIT)
                            .roomId(roomId)
                            .sender(name)
                            .build());
            chatRoomRepository.removeUserEnterInfo(sessionId);  //사용자 session 삭제
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
    }
}
