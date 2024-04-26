package com.example.backend.models;

import com.example.backend.enums.CurrencyEnum;
import com.example.backend.enums.TypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name="invoice")
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private TypeEnum type;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;
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

}
