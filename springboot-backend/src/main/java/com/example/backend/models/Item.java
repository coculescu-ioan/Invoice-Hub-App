package com.example.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name="item")
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long invoiceId;
    private String name;
    private int quantity;
    private BigDecimal unitPrice;

    public Item(long invoiceId, String name, int quantity, BigDecimal unitPrice) {
        this.invoiceId = invoiceId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
