package com.pick_me.backend.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

/*    @NotBlank(message = "Login cannot be blank")
    @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")*/
    @Schema(description = "User login", example = "nica")
    private String login;

    /*@NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")*/
    @Schema(description = "User password", example = "qwhdtags3j2!")
    private String password;
}
