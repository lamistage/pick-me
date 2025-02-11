package com.pick_me.backend.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
public class User {
    @Schema(description = "Tag identifier", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Schema(description = "User login", example = "nica")
    @Column(name = "login")
    private String login;

    @Schema(description = "User password", example = "qwhdtags3j2!")
    @Column(name = "password")
    private String password;
}
