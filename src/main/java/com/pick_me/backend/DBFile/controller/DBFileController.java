package com.pick_me.backend.DBFile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequestMapping("/api/db-file")
@Tag(name = "DBFile", description = "Allows to get info about all files, loaded by user to the system")
@RestController
public interface DBFileController {
    @Operation(
            summary = "Download file by id",
            description = "Method allows to download files by identifier"
    )
    @ApiResponse(
            responseCode = "200",
            description = "The file was successfully received. The header will contain the file name, and the body will contain an array of bytes of the file"
    )
    @GetMapping("/{fileId}")
    ResponseEntity<Resource> downloadFile(
            @Parameter(
                    description = "Entity id",
                    required = true,
                    example = "65ffdf-54bvvb-54f5ddfdf5df54fd-5cg4cgcg"
            ) @PathVariable String fileId);


    @Operation(
            summary = "Save user image",
            description = "Method, that allows to save a user image"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Associative array with paths to image files",
            content = @Content(
                    schema = @Schema(
                            example = "{\"imagePath\": \"value\"}"
                    )
            )
    )
    @PostMapping
    Map<String, String> saveFile (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Image file",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(
                                    type = "object",
                                    description = "Excel file"
                            )
                    )
            ) @RequestParam(required = false) MultipartFile image);
}
