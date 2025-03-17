package com.pick_me.backend.DBFile.service;

import com.pick_me.backend.DBFile.entity.DBFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface DBFileService {
    DBFile storeFile(MultipartFile file);

    DBFile getFile(String fileId);

    String saveImage(MultipartFile image);

    void removeFile(String fileId);
}
