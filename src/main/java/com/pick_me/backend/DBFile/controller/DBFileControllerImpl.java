package com.pick_me.backend.DBFile.controller;

import com.pick_me.backend.DBFile.entity.DBFile;
import com.pick_me.backend.DBFile.service.DBFileService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
public class DBFileControllerImpl implements DBFileController {
    private final DBFileService dbFileService;

    public DBFileControllerImpl(DBFileService dbFileService) {
        this.dbFileService = dbFileService;
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String fileId) {
        DBFile dbFile;
        CacheControl cacheControl = CacheControl.maxAge(5, TimeUnit.SECONDS).noTransform().mustRevalidate();
        dbFile = dbFileService.getFile(fileId);
        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .contentType(MediaType.parseMediaType(dbFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;")
                .body(new ByteArrayResource(dbFile.getData()));
    }

    @Override
    public Map<String, String> saveFile(MultipartFile image) {
        HashMap<String, String> objWithFilePath = new HashMap<>();
        objWithFilePath.put("filePath", dbFileService.saveImage(image));
        return objWithFilePath;
    }
}
