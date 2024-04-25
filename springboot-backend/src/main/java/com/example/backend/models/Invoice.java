package com.example.backend.models;

import com.example.backend.enums.Currency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Invoice {
    private String type;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private BigDecimal taxPercent;
    private String clientName;
    private String clientId;
    private String clientAddress;
    private String supplierName;
    private String supplierId;
    private String supplierAddress;
    private BigDecimal taxExclusiveAmount;
    private BigDecimal taxAmount;
    private BigDecimal taxInclusiveAmount;
    private List<Line> lines;
}
