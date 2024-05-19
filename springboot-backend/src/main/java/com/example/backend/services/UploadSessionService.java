package com.example.backend.services;

import com.example.backend.models.UploadSession;

import java.util.List;

public interface UploadSessionService {
    List<UploadSession> loadAll();
}
