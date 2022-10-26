package com.example.prj1513.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginInfo {
    private String name;    //사용자 ID
    private String token;   //jwt 토큰

    @Builder
    public LoginInfo(String name, String token){
        this.name = name;
        this.token = token;
    }
}
