package com.example.prj1513.controller;

import com.example.prj1513.model.ChatRoom;
import com.example.prj1513.model.LoginInfo;
import com.example.prj1513.repository.ChatRoomRepository;
import com.example.prj1513.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * [화면] 채팅방 리스트
     * @return String
     */
    @GetMapping("/room")
    public String rooms() {
        return "/chat/room";
    }

    /**
     * 채팅방 생성
     * @param name
     * @return ChatRoom
     */
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRoomRepository.createChatRoom(name);
    }

    /**
     * 채팅 리스트
     * @return List<ChatRoom>
     */
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllRoom();
        chatRooms.stream().forEach(room -> room.setUserCount(chatRoomRepository.getUserCount(room.getRoomId())));
        return chatRooms;
    }

    /**
     * [화면] 채팅방 상세
     * @param model
     * @param roomId
     */
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    /**
     * 채팅방 조회 by roomId
     * @param roomId
     * @return ChatRoom
     */
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    /**
     * 로그인 유저 정보 조회
     * @return LoginInfo
     */
    @GetMapping("/user")
    @ResponseBody
    public LoginInfo getUserInfo(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        LoginInfo loginInfo = LoginInfo.builder().name(name).token(jwtTokenProvider.generateToken(name)).build();
        return loginInfo;
    }
}
