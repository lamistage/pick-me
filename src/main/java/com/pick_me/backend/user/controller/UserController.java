package com.pick_me.backend.user.controller;

import com.pick_me.backend.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@Tag(name = "User", description = "Allows to get, add, update and remove info about user")
@RestController
public interface UserController {

    @Operation(
            summary = "Get user by id",
            description = "Method, that allows user get info about him by id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "User by id",
            content = @Content(
                    schema = @Schema(
                            implementation = User.class
                    )
            )
    )
    @GetMapping("/{userId}")
    User one(
            @Parameter(
                    description = "Entity id",
                    required = true,
                    example = "1"
            ) @PathVariable Integer userId) throws ChangeSetPersister.NotFoundException;


    @Operation(
            summary = "Saving of user",
            description = "Method, that allows save user in system"
    )
    @ApiResponse(
            responseCode = "201",
            description = "New user",
            content = @Content(
                    schema = @Schema(
                            implementation = User.class
                    )
            )
    )
    @PostMapping
    User save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Info about user",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    implementation = User.class
                            )
                    )
            ) @RequestBody User user);


    @Operation(
            summary = "Update of user",
            description = "Method, that allows user to update info about him"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Updated user",
            content = @Content(
                    schema = @Schema(
                            implementation = User.class
                    )
            )
    )
    @PutMapping
    User update(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Info about user",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    implementation = User.class
                            )
                    )
            ) @RequestBody User user);


    @Operation(
            summary = "Deletion of user by id",
            description = "Method, that allows user delete his account"
    )
    @ApiResponse(
            responseCode = "200, 204",
            description = "Deletion was successful"
    )
    @DeleteMapping("/{userId}")
    void remove(
            @Parameter(
                    description = "Entity id",
                    required = true,
                    example = "1"
            ) @PathVariable Integer userId) throws ChangeSetPersister.NotFoundException;
}
