package com.example.prj1513.common.interfaces;

import com.example.prj1513.model.ChatMessage;

public abstract class ChatService {
    public void sendMessage(ChatMessage chatMessage, boolean isRedis) {}

    public void pubMessage(ChatMessage chatMessage) {}
}
