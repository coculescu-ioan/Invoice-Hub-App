package com.example.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name="file_report")
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class FileReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private long uploadSessionId;
    private String filename;
    private String filetype;
    private LocalDate dateUploaded;
    private long size;
    private String status;

    public FileReport(long userId, long uploadSessionId,
                      String filename, String filetype,
                      LocalDate dateUploaded, long size, String status) {
        this.userId = userId;
        this.uploadSessionId = uploadSessionId;
        this.filename = filename;
        this.filetype = filetype;
        this.dateUploaded = dateUploaded;
        this.size = size;
        this.status = status;
    }
}
