package com.example.backend.controllers;

import com.example.backend.exceptions.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

public interface FileUploadController {
    String listUploadedFiles(Model model) throws IOException;
    ResponseEntity<Resource> serveFile(@PathVariable String filename);
    ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file);
    ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc);
}
