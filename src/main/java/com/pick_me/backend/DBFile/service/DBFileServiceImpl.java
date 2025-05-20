package com.pick_me.backend.DBFile.service;

import com.pick_me.backend.DBFile.entity.DBFile;
import com.pick_me.backend.DBFile.repository.DBFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class DBFileServiceImpl implements DBFileService{
    private final DBFileRepository dbFileRepository;

    public DBFileServiceImpl(DBFileRepository dbFileRepository) {
        this.dbFileRepository = dbFileRepository;
    }

    @Override
    public DBFile storeFile(MultipartFile file) {
        log.info("Storing file: originalName='{}', contentType='{}', size={} bytes", file.getOriginalFilename(), file.getContentType(), file.getSize());
        try {
            DBFile dbFile = new DBFile(file.getContentType(), file.getBytes());
            DBFile savedFile = dbFileRepository.save(dbFile);
            log.info("File stored successfully with id={}", savedFile.getId());
            return savedFile;
        } catch (IOException e) {
            log.error("Failed to read bytes from file '{}': {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public DBFile getFile(String fileId) {
        log.info("Retrieving file with id={}", fileId);
        return dbFileRepository.findById(fileId)
                .orElseThrow(() -> {
                    log.warn("File not found with id={}", fileId);
                    return new RuntimeException("File not found with id " + fileId);
                });
    }

    @Override
    public String saveImage(MultipartFile image) {
        log.info("Saving image file: originalName='{}'", image.getOriginalFilename());
        String path = "db-file/" + this.storeFile(image).getId();
        log.info("Image saved with path={}", path);
        return path;
    }

    @Override
    public void removeFile(String fileId) {
        log.info("Removing file with id={}", fileId);
        try {
            dbFileRepository.deleteById(fileId);
            log.info("File with id={} removed successfully", fileId);
        } catch (Exception e) {
            log.error("Failed to remove file with id={}: {}", fileId, e.getMessage(), e);
            throw e;
        }
    }
}
