package com.example.backend.repositories;

import com.example.backend.models.UploadSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadSessionRepository extends JpaRepository<UploadSession, Long> {
    List<UploadSession> findAllByUserId(long id);
}
