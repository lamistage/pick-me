package com.pick_me.backend.image.entity;

import com.pick_me.backend.tag.entity.Tag;
import com.pick_me.backend.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Schema(description = "Images")
public class Image {
    @Schema(description = "Entity identifier", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Schema(description = "Byte array representing the image", example="[2, 34, 76, 123, 57, 254]")
    @Lob
    @Column(name = "data")
    private byte[] data;

    @Schema(description = "Name of the file", example = "image1.jpg")
    @Column(name = "file_name")
    private String fileName;

    @Schema(description = "Type of the file", example = "jpg")
    @Column(name = "file_type")
    private String fileType;

    @Schema(description = "Image upload date", example = "Thu Feb 21 14:01:34 EET 2019")
    @Column(name = "date")
    private Date date;

    @Schema(description = "User, who load the image", example = "[id: 1, login = 'nica', password = 'qwhdtags3j2!']")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "List of tags linked to the image", example = "[{id: 1; name: 'kitty'}, {id: 2; name: 'sad'}]")
    @ManyToMany
    @JoinTable(name = "image_tag",
            joinColumns = {@JoinColumn(name = "image_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> Tags;
}
