package com.pick_me.backend.image.entity;

import com.pick_me.backend.dto.UserDTO;
import com.pick_me.backend.tag.entity.Tag;
import com.pick_me.backend.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Images")
public class Image implements Serializable {
    @Schema(description = "Entity identifier", example = "1")
    @Id
    @SequenceGenerator(name = "IMAGE_ID_GENERATOR", sequenceName = "image_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMAGE_ID_GENERATOR")
    private Integer id;

    @Schema(description = "Path to the image file", example="db-files/a5f9fca9-de57-439e-b1f2-eda7355e1419")
    @Column(name = "file_path")
    private String filePath;

    @Schema(description = "Image upload date", example = "Thu Feb 21 14:01:34 EET 2019")
    @Column(name = "date")
    @CreatedDate
    @LastModifiedDate
    private LocalDateTime date;

    @Schema(description = "User, who load the image", example = "[id: 1, login = 'nica']")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDTO user;

    @Schema(description = "List of tags linked to the image", example = "[{id: 1; name: 'kitty'}, {id: 2; name: 'sad'}]")
    @ManyToMany
    @JoinTable(name = "image_tag",
            joinColumns = {@JoinColumn(name = "image_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_name")})
    private List<Tag> tags;
}
