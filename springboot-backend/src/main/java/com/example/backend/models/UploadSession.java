package com.example.backend.models;

import com.example.backend.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name="upload_session")
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UploadSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    public UploadSession(long userId, LocalDateTime startTime, LocalDateTime endTime, StatusEnum status) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
