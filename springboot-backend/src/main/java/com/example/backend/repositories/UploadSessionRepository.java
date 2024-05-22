package com.example.backend.repositories;

import com.example.backend.models.UploadSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadSessionRepository extends JpaRepository<UploadSession, Long> {
    List<UploadSession> findAllByUserId(long id);

    @Query("SELECT u FROM UploadSession u ORDER BY u.endTime DESC LIMIT 6")
    List<UploadSession> findLastSessionsForAdmin();

    @Query("SELECT u FROM UploadSession u WHERE u.userId = :userId ORDER BY u.id DESC LIMIT 3")
    List<UploadSession> findLastSessionsForUser(long userId);
}
