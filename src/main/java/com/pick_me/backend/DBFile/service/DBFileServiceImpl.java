package com.pick_me.backend.DBFile.service;

import com.pick_me.backend.DBFile.entity.DBFile;
import com.pick_me.backend.DBFile.repository.DBFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DBFileServiceImpl implements DBFileService{
    private final DBFileRepository dbFileRepository;

    public DBFileServiceImpl(DBFileRepository dbFileRepository) {
        this.dbFileRepository = dbFileRepository;
    }

    @Override
    public DBFile storeFile(MultipartFile file) {
        try {
            DBFile dbFile = new DBFile(file.getContentType(), file.getBytes());
            return dbFileRepository.save(dbFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DBFile getFile(String fileId) {
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
    }

    @Override
    public String saveImage(MultipartFile image) {
        return "db-file/" + this.storeFile(image).getId();
    }
}
