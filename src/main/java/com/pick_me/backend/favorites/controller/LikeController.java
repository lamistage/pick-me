package com.pick_me.backend.favorites.controller;

import com.pick_me.backend.dto.ImageDTO;
import com.pick_me.backend.dto.UserDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/likes")
@Tag(name = "Likes", description = "Allows to manage likes (favorites) fro images")
@RestController
public interface LikeController {

    @Operation(
            summary = "Add image to user's favorites",
            description = "Method that allows a user to add an image to their favorites"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Image successfully added to favorites"
    )
    @ApiResponse(
            responseCode = "404",
            description = "User or image not found"
    )
    @PostMapping("/add")
    void addToFavorites (
        @Parameter(
                description = "User id",
                required = true,
                example = "1"
        ) @RequestParam Integer userId,
        @Parameter(
                description = "Image id",
                required = true,
                example = "2"
        ) @RequestParam Integer imageId);

    @Operation(
            summary = "Remove image from user's favorites",
            description = "Method that allows a user to remove an image from their favorites"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Image successfully removed from favorites"
    )
    @ApiResponse(
            responseCode = "404",
            description = "User or image not found"
    )
    @DeleteMapping("/remove")
    void removeFromFavorites(
            @Parameter(
                    description = "User id",
                    required = true,
                    example = "1"
            ) @RequestParam Integer userId,
            @Parameter(
                    description = "Image id",
                    required = true,
                    example = "2"
            ) @RequestParam Integer imageId);

    @Operation(
            summary = "Get user's favorite images",
            description = "Method that returns a list of images added to the user's favorites"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of favorite images",
            content = @Content(
                    array = @ArraySchema(
                            schema = @Schema(implementation = ImageDTO.class)
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not found"
    )
    @GetMapping("/favorites")
    @PageableAsQueryParam
    Page<ImageDTO> getFavorites(
            @Parameter(
                    description = "User id",
                    required = true,
                    example = "1"
            ) @RequestParam Integer userId,
            Pageable pageable,
            @Parameter(
                    description = "List of tags",
                    array = @ArraySchema(
                            schema = @Schema(implementation = String.class)
                    )
            ) @RequestParam(value = "tag", required = false) List<String> tags,
            @Parameter(
                    description = "List of logins of users",
                    array = @ArraySchema(
                            schema = @Schema(implementation = String.class)
                    )
            ) @RequestParam(value = "user", required = false) List<String> userLogins,
            @Parameter(
                    description = "The parameters by which the sorting will take place. The first value is a parameter, the second is a method (desc, asc). Separated by commas.",
                    example = "date,desc",
                    content = @Content(
                            schema = @Schema(allowableValues = {"date"})
                    )
            ) @RequestParam(value = "sort", required = false) String sort);

    @Operation(
            summary = "Get users who liked an image",
            description = "Method that returns a list of users who added the image to their favorites"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of users who liked the image",
            content = @Content(
                    array = @ArraySchema(
                            schema = @Schema(implementation = UserDTO.class)
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Image not found"
    )
    @GetMapping("/liked-by")
    List<UserDTO> getUsersWhoLiked(
            @Parameter(
                    description = "Image ID",
                    required = true,
                    example = "2"
            ) @RequestParam Integer imageId);

    @Operation(
            summary = "Check if user liked an image",
            description = "Method that checks if a specific user has added an image to their favorites"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Boolean indicating if the user liked the image",
            content = @Content(
                    schema = @Schema(implementation = Boolean.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "User or image not found"
    )
    @GetMapping("/is-liked")
    Boolean isLikedByUser(
            @Parameter(
                    description = "User id",
                    required = true,
                    example = "1"
            ) @RequestParam Integer userId,
            @Parameter(
                    description = "Image id",
                    required = true,
                    example = "2"
            ) @RequestParam Integer imageId);
}
