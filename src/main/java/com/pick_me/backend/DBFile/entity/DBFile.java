package com.pick_me.backend.DBFile.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
@Table(name = "files")
public class DBFile {
    @Schema(description = "Entity identifier", example = "1")
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Schema(description = "Type of the file", example = "jpg")
    @Column(name = "file_type")
    private String fileType;

    @Schema(description = "Byte array of the file", example = "[12, 35, 243, 34]")
    @Lob
    private byte[] data;


    public DBFile(String fileType, byte[] data) {
        this.fileType = fileType;
        this.data = data;
    }
}
