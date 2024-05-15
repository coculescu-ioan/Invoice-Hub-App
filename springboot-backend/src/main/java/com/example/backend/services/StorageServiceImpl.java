package com.example.backend.services;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import java.util.stream.Stream;

import com.example.backend.enums.CurrencyEnum;
import com.example.backend.enums.StatusEnum;
import com.example.backend.enums.TypeEnum;
import com.example.backend.enums.ValidationRulesEnum;
import com.example.backend.exceptions.StorageException;
import com.example.backend.exceptions.StorageFileNotFoundException;
import com.example.backend.models.FileReport;
import com.example.backend.models.Invoice;
import com.example.backend.models.Item;
import com.example.backend.models.UploadSession;
import com.example.backend.properties.StorageProperties;
import com.example.backend.repositories.FileReportRepository;
import com.example.backend.repositories.InvoiceRepository;
import com.example.backend.repositories.ItemRepository;
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
    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public StorageServiceImpl(StorageProperties properties,
                              FileReportRepository fileReportRepository,
                              UploadSessionRepository uploadSessionRepository,
                              InvoiceRepository invoiceRepository,
                              ItemRepository itemRepository) {

        this.fileReportRepository = fileReportRepository;
        this.uploadSessionRepository = uploadSessionRepository;
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;

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

            if (isStructureValid(file.getInputStream(), lines, errors)) {
                if (areCalculationsCorrect(file.getInputStream(), errors)) {

                    Invoice invoice = buildInvoice(lines);
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
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .filter(Files::isRegularFile)
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

    private ResponseEntity<?> handleUploadResponse(UploadSession session, StatusEnum status,
                                                   String message, HttpStatus httpStatus) {
        session.setStatus(status);
        session.setEndTime(LocalDateTime.now());
        uploadSessionRepository.save(session);
        return ResponseEntity.status(httpStatus).body(message);
    }

    private static Invoice buildInvoice(List<String> lines) {
        Invoice invoice = new Invoice();
        invoice.setType(TypeEnum.valueOf(lines.get(0).split("=")[1].toUpperCase()));
        invoice.setDate(LocalDate.parse(lines.get(1).split("=")[1]));
        invoice.setCurrency(CurrencyEnum.valueOf(lines.get(2).split("=")[1].toUpperCase()));
        invoice.setTaxPercent(new BigDecimal(lines.get(3).split("=")[1]));

        invoice.setClientName(lines.get(4).split("=")[1]);
        invoice.setClientId(lines.get(5).split("=")[1]);
        invoice.setClientAddress(lines.get(6).split("=")[1]);

        invoice.setSupplierName(lines.get(7).split("=")[1]);
        invoice.setSupplierId(lines.get(8).split("=")[1]);
        invoice.setSupplierAddress(lines.get(9).split("=")[1]);

        invoice.setTaxExclusiveAmount(new BigDecimal(lines.get(10).split("=")[1]));
        invoice.setTaxAmount(new BigDecimal(lines.get(11).split("=")[1]));
        invoice.setTaxInclusiveAmount(new BigDecimal(lines.get(12).split("=")[1]));

        return invoice;
    }

    private static boolean isStructureValid(InputStream contents, List<String> lines, List<String> errors) throws IOException {
        int lineIndex = 0;
        int validLines = 0;
        int itemCounter = 0;

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(contents))) {
            String line;
            while((line = reader.readLine()) != null) {
                lineIndex++;
                lines.add(line);
                ValidationRulesEnum rule = ValidationRulesEnum.findByLineIndex(lineIndex);

                if(rule.isValid(line)) {
                    validLines++;
                    if(lineIndex > 13) {
                        itemCounter++;
                    }
                }
                else {
                    errors.add(rule.getErrorMessage());
                }
            }
            if(itemCounter == 0) {
                errors.add("NO valid items found");
            }
        }

        return validLines == lineIndex && itemCounter > 0;
    }

    private boolean areCalculationsCorrect(InputStream contents, List<String> errors) throws IOException {
        BigDecimal taxExclusiveAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal taxInclusiveAmount = BigDecimal.ZERO;
        BigDecimal taxPercent = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(contents))) {
            String line;
            int lineIndex = 0;

            while ((line = reader.readLine()) != null) {
                ValidationRulesEnum rule = ValidationRulesEnum.findByLineIndex(++lineIndex);

                if (rule == ValidationRulesEnum.TAX_EXCLUSIVE_AMOUNT) {
                    String[] parts = line.split("=");
                    taxExclusiveAmount = new BigDecimal(parts[1]);
                }
                else if (rule == ValidationRulesEnum.TAX_AMOUNT) {
                    String[] parts = line.split("=");
                    taxAmount = new BigDecimal(parts[1]);
                }
                else if (rule == ValidationRulesEnum.TAX_INCLUSIVE_AMOUNT) {
                    String[] parts = line.split("=");
                    taxInclusiveAmount = new BigDecimal(parts[1]);
                }
                else if (rule == ValidationRulesEnum.TAX_PERCENT) {
                    String[] parts = line.split("=");
                    taxPercent = new BigDecimal(parts[1]);
                }
                else if (rule == ValidationRulesEnum.ITEM) {
                    String[] parts = line.substring(5).split(",");
                    int quantity = Integer.parseInt(parts[1]);
                    BigDecimal unitPrice = new BigDecimal(parts[2]);
                    totalValue = totalValue.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
                }
            }
        }

        errors.add(String.format("TaxPercent: %.3f TotalValue: %.3f", taxPercent, totalValue));

        BigDecimal taxRatio = taxPercent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        errors.add(String.format("Tax Ratio: %.3f", taxRatio));

        BigDecimal expectedTaxAmount = taxRatio.multiply(totalValue);
        BigDecimal expectedTaxInclusiveAmount = taxExclusiveAmount.add(expectedTaxAmount);
        errors.add(String.format("ExpectedTaxAmount: %.3f Found: %.3f", expectedTaxAmount, taxAmount));
        errors.add(String.format("ExpectedTaxInclusiveAmount: %.3f Found: %.3f", expectedTaxInclusiveAmount, taxInclusiveAmount));

        return taxAmount.compareTo(expectedTaxAmount) == 0
                && taxInclusiveAmount.compareTo(expectedTaxInclusiveAmount) == 0;

    }
}
