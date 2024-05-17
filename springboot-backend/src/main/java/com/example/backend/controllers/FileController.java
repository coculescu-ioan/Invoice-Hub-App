package com.example.backend.controllers;

import com.example.backend.exceptions.StorageFileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileController {
    ResponseEntity<?> listUploadedFiles() throws IOException;
    ResponseEntity<?> serveFile(@PathVariable String filename);
    ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file);
    ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc);

}
