package com.example.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name="uploadsession")
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class UploadSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public UploadSession(long userId, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
