package com.pick_me.backend.tag.entity;

import com.pick_me.backend.image.entity.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Tag {
    @Schema(description = "Tag identifier", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Schema(description = "Name of the tag", example = "kitty")
    @Column(name = "name")
    private String name;
}
