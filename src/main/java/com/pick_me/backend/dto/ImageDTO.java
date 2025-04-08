package com.pick_me.backend.dto;

import com.pick_me.backend.tag.entity.Tag;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ImageDTO {
    private Integer id;
    private String filePath;
    private LocalDateTime date;
    private UserDTO user;
    private List<Tag> tags;
}
