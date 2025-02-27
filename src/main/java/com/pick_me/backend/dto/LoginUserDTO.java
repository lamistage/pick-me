package com.pick_me.backend.dto;

import com.pick_me.backend.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Entity
@Table(name = "app_user")
@Schema(description = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LoginUserDTO implements Serializable {
    @Id
    @Schema(description = "User login", example = "nica")
    private String login;

    @Schema(description = "User password", example = "12345678")
    private String password;

    public LoginUserDTO(User user) {
        this.login = user.getLogin() != null ? user.getLogin() : null;
        this.password = user.getPassword() != null? user.getPassword() : null;
    }
}
