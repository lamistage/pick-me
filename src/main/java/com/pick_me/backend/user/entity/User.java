package com.pick_me.backend.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "app_user")
@Data
public class User implements Serializable {
    @Schema(description = "Tag identifier", example = "1")
    @Id
    @SequenceGenerator(name = "USER_ID_GENERATOR", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_GENERATOR")
    private Integer id;

    @Schema(description = "User login", example = "nica")
    private String login;

    @Schema(description = "User password", example = "qwhdtags3j2!")
    private String password;
}
