package com.example.helloserver.vo;

import lombok.Data;

@Data
public class UserDetailVo {
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String address;
}