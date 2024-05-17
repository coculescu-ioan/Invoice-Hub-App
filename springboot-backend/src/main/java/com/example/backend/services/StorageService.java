package com.example.backend.services;


import com.example.backend.models.FileReport;
import com.example.backend.models.UploadSession;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface StorageService {
    void init();

    ResponseEntity<?> store(MultipartFile file);

    List<FileReport> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    List<UploadSession> loadAllUploadSessions();

}
