package com.example.backend.services;

import com.example.backend.enums.CurrencyEnum;
import com.example.backend.enums.TypeEnum;
import com.example.backend.enums.UserRoleEnum;
import com.example.backend.enums.ValidationRulesEnum;
import com.example.backend.models.Invoice;
import com.example.backend.models.User;
import com.example.backend.repositories.InvoiceRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService{

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Invoice buildInvoice(List<String> lines) {
        Invoice invoice = new Invoice();

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            invoice.setUserId(user.getId());
        }
        else {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }

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

    @Override
    public List<Invoice> loadAll() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return Objects.equals(user.getRole(), UserRoleEnum.ADMIN) ?
                    invoiceRepository.findAll() :
                    invoiceRepository.findAllByUserId(user.getId());
        }
        else {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
    }

    @Override
    public boolean isStructureValid(InputStream contents, List<String> lines, List<String> errors) throws IOException {
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

    @Override
    public boolean areCalculationsCorrect(InputStream contents, List<String> errors) throws IOException {
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
