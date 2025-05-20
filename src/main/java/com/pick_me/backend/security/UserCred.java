package com.pick_me.backend.security;

import lombok.Data;

@Data
public class UserCred {
    private String loginOrEmail;
    private String password;
}
