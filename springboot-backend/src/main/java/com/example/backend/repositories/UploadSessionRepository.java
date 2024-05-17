package com.example.backend.repositories;

import com.example.backend.models.UploadSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadSessionRepository extends JpaRepository<UploadSession, Long> {
    List<UploadSession> findAllByUserId(long id);
}
