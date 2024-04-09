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
@AllArgsConstructor
public class UploadSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

}
