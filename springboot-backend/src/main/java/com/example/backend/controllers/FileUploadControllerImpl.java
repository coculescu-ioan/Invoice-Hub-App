package com.example.backend.controllers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.example.backend.exceptions.StorageFileNotFoundException;
import com.example.backend.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Controller
@RequestMapping("/api/upload")
public class FileUploadControllerImpl implements FileUploadController{
    private final StorageService storageService;

    @Autowired
    public FileUploadControllerImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public ResponseEntity<List<String>> listUploadedFiles() throws IOException {
        List<String> fileUrls = storageService.loadAll()
                .map(path -> MvcUriComponentsBuilder.fromMethodName(FileUploadControllerImpl.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(fileUrls);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        if (file == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/file")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        return storageService.store(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
