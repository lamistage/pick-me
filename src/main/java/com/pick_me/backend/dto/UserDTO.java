package com.pick_me.backend.dto;

import com.pick_me.backend.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "app_user")
@Schema(description = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    @Id
    @Schema(description = "User id", example = "1")
    private Integer id;

    @Schema(description = "User login", example = "nica")
    private String login;

    public UserDTO(User user) {
        this.id = user.getId() != null ? user.getId() : null;
        this.login = user.getLogin() != null ? user.getLogin() : null;
    }
}
