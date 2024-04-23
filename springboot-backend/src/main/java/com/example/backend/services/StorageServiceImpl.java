package com.example.backend.services;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import com.example.backend.exceptions.StorageException;
import com.example.backend.exceptions.StorageFileNotFoundException;
import com.example.backend.models.FileReport;
import com.example.backend.models.UploadSession;
import com.example.backend.properties.StorageProperties;
import com.example.backend.repositories.FileReportRepository;
import com.example.backend.repositories.UploadSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;
    private final FileReportRepository fileReportRepository;
    private final UploadSessionRepository uploadSessionRepository;

    @Autowired
    public StorageServiceImpl(StorageProperties properties,
                              FileReportRepository fileReportRepository,
                              UploadSessionRepository uploadSessionRepository) {

        this.fileReportRepository = fileReportRepository;
        this.uploadSessionRepository = uploadSessionRepository;

        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public ResponseEntity<?> store(MultipartFile file) {
        UploadSession session = new UploadSession();
        // User Identification to be implemented after JWT
        session.setUserId(1);
        session.setStartTime(LocalDateTime.now());
        session.setStatus("In progress...");
        session = uploadSessionRepository.save(session);
        try {
            if (file.isEmpty()) {
                session.setStatus("Failed");
                session.setEndTime(LocalDateTime.now());
                session = uploadSessionRepository.save(session);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                session.setStatus("Failed");
                session.setEndTime(LocalDateTime.now());
                session = uploadSessionRepository.save(session);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cannot store file outside current directory.");
            }
            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setStatus("Success");
            session.setEndTime(LocalDateTime.now());

            storeFileReport(file, session);

            uploadSessionRepository.save(session);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("File successfully uploaded: " + file.getOriginalFilename());
        } catch (IOException e) {
            session.setStatus("Failed");
            session.setEndTime(LocalDateTime.now());
            uploadSessionRepository.save(session);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to store file: " + e.getMessage());
        }
    }

    private void storeFileReport(MultipartFile file, UploadSession session) {
        FileReport fileReport = new FileReport();
        fileReport.setUploadSessionId(session.getId());
        fileReport.setUserId(session.getUserId());
        fileReport.setFilename(file.getOriginalFilename());
        fileReport.setFiletype(file.getContentType());
        fileReport.setSize(file.getSize());
        fileReport.setDateUploaded(session.getEndTime().toLocalDate());
        fileReport.setStatus(session.getStatus());
        fileReportRepository.save(fileReport);
    }

    @Override
    public Stream<Path> loadAll() {
        try (Stream<Path> paths = Files.walk(this.rootLocation, 1)) {
            return paths.filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }


    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
