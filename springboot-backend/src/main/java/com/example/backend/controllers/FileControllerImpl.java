package com.example.backend.controllers;

import java.io.IOException;

import com.example.backend.exceptions.StorageFileNotFoundException;
import com.example.backend.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/file")
public class FileControllerImpl implements FileController {
    private final StorageService storageService;

    @Autowired
    public FileControllerImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        return storageService.store(file);
    }

    // frontend:FilesComponent
    @GetMapping("/getAll")
    public ResponseEntity<?> listUploadedFiles() throws IOException {
        return ResponseEntity.ok(storageService.loadAll());
    }

    // frontend: StorageService (download file)
    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        if (file == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    // frontend: SessionsComponent
    @GetMapping("/uploadSessions")
    public ResponseEntity<?> getSessions() {
        return ResponseEntity.ok(storageService.loadAllUploadSessions());
    }

}
