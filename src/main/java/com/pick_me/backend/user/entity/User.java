package com.pick_me.backend.user.entity;

import com.pick_me.backend.image.entity.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@SuperBuilder
public class User implements Serializable, UserDetails {
    @Schema(description = "Tag identifier", example = "1")
    @Id
    @SequenceGenerator(name = "USER_ID_GENERATOR", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_GENERATOR")
    private Integer id;

    @Schema(description = "User email", example = "qwerty@gmail.com")
    @Email
    private String email;

    @Schema(description = "User login", example = "nica")
    private String login;

    @Schema(description = "User password", example = "qwhdtags3j2!")
    private String password;

    @Schema(description = "List of images added to favorites by user")
    @ManyToMany
    @JoinTable(
            name = "user_image_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> favorites = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public  boolean  isAccountNonExpired () {
        return  true ;
    }

    @Override
    public  boolean  isAccountNonLocked () {
        return  true ;
    }

    @Override
    public  boolean  isCredentialsNonExpired () {
        return  true ;
    }

    @Override
    public  boolean  isEnabled () {
        return  true ;
    }
}
