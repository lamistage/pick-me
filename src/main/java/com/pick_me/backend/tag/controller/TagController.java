package com.pick_me.backend.tag.controller;

import com.pick_me.backend.tag.entity.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/tag")
@RestController
public interface TagController {
    @PostMapping
    Tag save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tag name",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    implementation = Tag.class
                            )
                    )
            ) @RequestBody Tag tag);
}
