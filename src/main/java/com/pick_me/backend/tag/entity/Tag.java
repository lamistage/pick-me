package com.pick_me.backend.tag.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Tag implements Serializable {
    @Schema(description = "Name of the tag", example = "kitty")
    @Id
    private String name;
}
