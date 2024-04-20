package com.example.backend.repositories;

import com.example.backend.models.FileReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileReportRepository extends JpaRepository<FileReport, Long> {
}
