package com.example.prj1513.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessage {

    //메세지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String message, long userCount){
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.userCount = userCount;
    }

    private MessageType type;   //메세지 타입
    private String roomId;  //방번호
    private String sender;  //발신자
    private String message; //메세지 내용
    private long userCount; //사용자 수
}
