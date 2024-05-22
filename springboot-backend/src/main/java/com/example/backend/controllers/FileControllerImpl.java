package com.example.backend.controllers;

import java.io.IOException;
import java.util.List;

import com.example.backend.exceptions.StorageFileNotFoundException;
import com.example.backend.models.UploadSession;
import com.example.backend.services.InvoiceService;
import com.example.backend.services.StorageService;
import com.example.backend.services.UploadSessionService;
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
    private final InvoiceService invoiceService;
    private final UploadSessionService uploadSessionService;

    @Autowired
    public FileControllerImpl(StorageService storageService, InvoiceService invoiceService, UploadSessionService uploadSessionService) {
        this.storageService = storageService;
        this.invoiceService = invoiceService;
        this.uploadSessionService = uploadSessionService;
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

//    @GetMapping("/uploadSessions")
//    public ResponseEntity<?> getSessions(@RequestParam int limit) {
//        List<UploadSession> sessions = uploadSessionService.findLastSessions();
//        return ResponseEntity.ok(sessions);
//    }

    @GetMapping("/lastSessions")
    public ResponseEntity<?> getLastSessions() {
        List<UploadSession> sessions = uploadSessionService.findLastSessions();
        return ResponseEntity.ok(sessions);
    }

    // frontend: InvoicesComponent
    @GetMapping("/invoices")
    public ResponseEntity<?> getInvoices() {
        return ResponseEntity.ok(invoiceService.loadAll());
    }

}
