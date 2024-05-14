package com.example.backend.models;

import com.example.backend.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name="file_report")
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
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
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    public FileReport(long userId, long uploadSessionId,
                      String filename, String filetype,
                      LocalDate dateUploaded, long size, StatusEnum status) {
        this.userId = userId;
        this.uploadSessionId = uploadSessionId;
        this.filename = filename;
        this.filetype = filetype;
        this.dateUploaded = dateUploaded;
        this.size = size;
        this.status = status;
    }
}
