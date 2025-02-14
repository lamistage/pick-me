package com.pick_me.backend.image.controller;

import com.pick_me.backend.image.entity.Image;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/image")
@Tag(name = "Image", description = "Allows to get, update, add and delete info about image")
@RestController
public interface ImageController {
    @Operation(
            summary = "Get paginated list of images with filter",
            description = "Method, that allows to get paginated list of images with filter"
    )
    @GetMapping
    @PageableAsQueryParam
    Page<Image> list(
            Pageable pageable,
            @Parameter(
                    description = "Names of parameters to sort by",
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = String.class
                            )
                    )
            ) @RequestParam(value = "sort", required = false) List<String> sort,
            @Parameter(
                    description = "List of identifiers of tags",
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = Integer.class
                            )
                    )
            ) @RequestParam(value = "tag", required = false) List<Integer> tags,
            @Parameter(
                    description = "List of identifiers users",
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = Integer.class
                            )
                    )
            ) @RequestParam(value = "user", required = false) List<Integer> users);

    @Operation(
            summary = "Get image by id",
            description = "Method, that allows to get an image by id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Image by id",
            content = @Content(
                    schema = @Schema(
                            implementation = Image.class
                    )
            )
    )
    @GetMapping("/{imageId}")
    Image one(
            @Parameter(
                    description = "Entity id",
                    required = true,
                    example = "1"
            ) @PathVariable Integer imageId) throws ChangeSetPersister.NotFoundException;

    @Operation(
            summary = "Save image",
            description = "Method, that allows to save an image in system"
    )
    @ApiResponse(
            responseCode = "201",
            description = "New image",
            content = @Content(
                    schema = @Schema(
                            implementation = Image.class
                    )
            )
    )
    @PostMapping
    Image save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Info about image",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    implementation = Image.class
                            )
                    )
            ) @RequestBody Image image);


    @Operation(
            summary = "Image update",
            description = "Method, that allows update info about image by id"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Updated image",
            content = @Content(
                    schema = @Schema(
                            implementation = Image.class
                    )
            )
    )
    @PutMapping
    Image update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Info about image",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    implementation = Image.class
                            )
                    )
            ) @RequestBody Image image);


    @Operation(
            summary = "Deletion of image by id",
            description = "Method, that allows to delete an image by id"
    )
    @ApiResponse(
            responseCode = "200, 204",
            description = "Deletion was successful"
    )
    @DeleteMapping("/{imageId}")
    void remove(
            @Parameter(
                    description = "Entity id",
                    required = true,
                    example = "1"
            ) @PathVariable Integer imageId);
}
