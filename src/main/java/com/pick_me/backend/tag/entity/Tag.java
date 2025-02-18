package com.pick_me.backend.tag.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Tag implements Serializable {
    @Schema(description = "Tag identifier", example = "1")
    @Id
    @SequenceGenerator(name = "TAG_ID_GENERATOR", sequenceName = "tag_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAG_ID_GENERATOR")
    private Integer id;

    @Schema(description = "Name of the tag", example = "kitty")
    private String name;
}
