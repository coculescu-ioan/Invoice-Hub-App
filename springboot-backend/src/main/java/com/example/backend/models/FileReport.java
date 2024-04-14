package com.example.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name="filereport")
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
    private String filename;
    private String filetype;
    private LocalDate dateUploaded;
    private long size;
    private long userId;
    private String status;

    public FileReport(String filename, String filetype, LocalDate dateUploaded, long size, long userId, String status) {
        this.filename = filename;
        this.filetype = filetype;
        this.dateUploaded = dateUploaded;
        this.size = size;
        this.userId = userId;
        this.status = status;
    }
}
