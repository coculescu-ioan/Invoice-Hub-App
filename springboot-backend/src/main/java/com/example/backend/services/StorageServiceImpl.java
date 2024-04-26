package com.example.backend.services;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import com.example.backend.enums.ValidationRulesEnum;
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
        session.setUserId(1); // User Identification to be implemented after JWT
        session.setStartTime(LocalDateTime.now());
        session.setStatus("Pending");
        session = uploadSessionRepository.save(session);

        try {
            if (file.isEmpty()) {
                return handleUploadResponse(session, "Failed", "Cannot store empty file.",
                        HttpStatus.BAD_REQUEST);
            }

            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                return handleUploadResponse(session, "Failed",
                        "Cannot store file outside current directory.",
                        HttpStatus.BAD_REQUEST);
            }

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            if (isStructureValid(file.getInputStream())) {
                if (areCalculationsCorrect(file.getInputStream())) {

                    storeFileReport(file, session);
                    return handleUploadResponse(session, "Success",
                            "File successfully uploaded: " + file.getOriginalFilename(),
                            HttpStatus.OK);
                } else {
                    return handleUploadResponse(session, "Failed", "Please review calculations.",
                            HttpStatus.BAD_REQUEST);
                }
            } else {
                return handleUploadResponse(session, "Failed", "Invalid file structure.",
                        HttpStatus.BAD_REQUEST);
            }

        } catch (IOException e) {
            return handleUploadResponse(session, "Failed", "Unable to store file: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> handleUploadResponse(UploadSession session, String status,
                                                   String message, HttpStatus httpStatus) {
        session.setStatus(status);
        session.setEndTime(LocalDateTime.now());
        uploadSessionRepository.save(session);
        return ResponseEntity.status(httpStatus).body(message);
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

    // Function to check matching the structure of Invoice/CreditNote
    private static boolean isStructureValid(InputStream contents) throws IOException {
        int lineIndex = 0;
        int validLines = 0;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(contents))) {

            String line;
            while((line = reader.readLine()) != null) {
                lineIndex++;
                ValidationRulesEnum rule = ValidationRulesEnum.findByLineIndex(lineIndex);

                if(rule.isValid(line)) {
                    System.out.printf("Line %d (%s) is valid\n", lineIndex, rule.name());
                    validLines++;
                }
                else {
                    System.out.printf("Line %d (%s) is not valid\nLine content: %s", lineIndex, rule.name(), line);
                }
            }
        }

        return validLines == lineIndex;
    }

    private boolean areCalculationsCorrect(InputStream contents) throws IOException {
        double taxExclusiveAmount = 0;
        double taxAmount = 0;
        double taxInclusiveAmount = 0;
        double taxPercent = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(contents))) {
            String line;
            int lineIndex = 0;
            double totalValue = 0;

            while ((line = reader.readLine()) != null) {
                ValidationRulesEnum rule = ValidationRulesEnum.findByLineIndex(++lineIndex);

                if (rule == ValidationRulesEnum.TAX_EXCLUSIVE_AMOUNT) {
                    String[] parts = line.split("=");
                    taxExclusiveAmount = Double.parseDouble(parts[1]);
                } else if (rule == ValidationRulesEnum.TAX_AMOUNT) {
                    String[] parts = line.split("=");
                    taxAmount = Double.parseDouble(parts[1]);
                } else if (rule == ValidationRulesEnum.TAX_INCLUSIVE_AMOUNT) {
                    String[] parts = line.split("=");
                    taxInclusiveAmount = Double.parseDouble(parts[1]);
                } else if (rule == ValidationRulesEnum.TAX_PERCENT) {
                    String[] parts = line.split("=");
                    taxPercent = Double.parseDouble(parts[1]);
                } else if (rule == ValidationRulesEnum.ITEM) {
                    String[] parts = line.substring(5).split("/");
                    int quantity = Integer.parseInt(parts[1]);
                    double unitPrice = Double.parseDouble(parts[2]);
                    totalValue += quantity * unitPrice;
                }
            }
        }

        double expectedTaxAmount = (taxPercent / 100) * taxExclusiveAmount;
        double expectedTaxExclusiveAmount = taxExclusiveAmount + expectedTaxAmount;

        return Math.abs(taxAmount - expectedTaxAmount) < 0.01
                && Math.abs(expectedTaxExclusiveAmount - taxInclusiveAmount) < 0.01;
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
