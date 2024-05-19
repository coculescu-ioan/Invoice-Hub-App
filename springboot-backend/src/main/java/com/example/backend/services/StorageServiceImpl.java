package com.example.backend.services;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.backend.enums.*;
import com.example.backend.exceptions.StorageException;
import com.example.backend.exceptions.StorageFileNotFoundException;
import com.example.backend.models.*;
import com.example.backend.properties.StorageProperties;
import com.example.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;
    private final FileReportRepository fileReportRepository;
    private final UploadSessionRepository uploadSessionRepository;
    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    public StorageServiceImpl(StorageProperties properties,
                              FileReportRepository fileReportRepository,
                              UploadSessionRepository uploadSessionRepository,
                              InvoiceRepository invoiceRepository,
                              ItemRepository itemRepository, UserRepository userRepository) {

        this.fileReportRepository = fileReportRepository;
        this.uploadSessionRepository = uploadSessionRepository;
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;

        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public ResponseEntity<?> store(MultipartFile file) {
        // Extract username from current session
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        User user = optionalUser.get();

        UploadSession session = new UploadSession();
        session.setUserId(user.getId());
        session.setStartTime(LocalDateTime.now());
        session.setStatus(StatusEnum.PENDING);
        session = uploadSessionRepository.save(session);

        FileReport fileReport = new FileReport();
        fileReport.setUploadSessionId(session.getId());
        fileReport.setUserId(session.getUserId());

        fileReport.setFilename(file.getOriginalFilename());
        fileReport.setFiletype(file.getContentType());
        fileReport.setSize(file.getSize());

        fileReport.setDateUploaded(LocalDate.now());

        try {
            if (file.isEmpty()) {
                fileReport.setStatus(StatusEnum.FAILED);
                fileReportRepository.save(fileReport);
                return handleUploadResponse(session, StatusEnum.FAILED, "Cannot store empty file.",
                        HttpStatus.BAD_REQUEST);
            }

            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                fileReport.setStatus(StatusEnum.FAILED);
                fileReportRepository.save(fileReport);
                return handleUploadResponse(session, StatusEnum.FAILED,
                        "Cannot store file outside current directory.",
                        HttpStatus.BAD_REQUEST);
            }

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            fileReport.setStatus(StatusEnum.SUCCESS);
            fileReportRepository.save(fileReport);

            List<String> lines = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            if (invoiceService.isStructureValid(file.getInputStream(), lines, errors)) {
                if (invoiceService.areCalculationsCorrect(file.getInputStream(), errors)) {

                    Invoice invoice = invoiceService.buildInvoice(lines);
                    invoice = invoiceRepository.save(invoice);
                    for(int i=13; i<lines.size(); i++) {
                        String[] parts = lines.get(i).substring(5).split(",");
                        itemRepository.save(new Item(
                                invoice.getId(),
                                parts[0],
                                Integer.parseInt(parts[1]),
                                new BigDecimal(parts[2])
                        ));
                    }

                    return handleUploadResponse(session, StatusEnum.SUCCESS,
                            "Invoice successfully validated: " + file.getOriginalFilename(),
                            HttpStatus.OK);
                } else {
                    String body = "Please review calculations:\n" + String.join("\n", errors);
                    return handleUploadResponse(session, StatusEnum.FAILED, body,
                            HttpStatus.BAD_REQUEST);
                }
            } else {
                String body = "Invalid file structure:\n" + String.join("\n", errors);
                return handleUploadResponse(session, StatusEnum.FAILED, body,
                        HttpStatus.BAD_REQUEST);
            }

        } catch (IOException e) {
            fileReport.setStatus(StatusEnum.FAILED);
            fileReportRepository.save(fileReport);
            return handleUploadResponse(session, StatusEnum.FAILED, "Unable to store file: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<FileReport> loadAll() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return Objects.equals(user.getRole(), UserRoleEnum.ADMIN) ?
                    fileReportRepository.findAll() :
                    fileReportRepository.findAllByUserId(user.getId());
        }
        else {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
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

    private ResponseEntity<?> handleUploadResponse(UploadSession session, StatusEnum status,
                                                   String message, HttpStatus httpStatus) {
        session.setStatus(status);
        session.setEndTime(LocalDateTime.now());
        uploadSessionRepository.save(session);
        return ResponseEntity.status(httpStatus).body(message);
    }

}
