package com.example.backend.services;

import com.example.backend.models.Invoice;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface InvoiceService {
    Invoice buildInvoice(List<String> lines);
    List<Invoice> loadAll();
    boolean isStructureValid(InputStream contents, List<String> lines, List<String> errors) throws IOException;
    boolean areCalculationsCorrect(InputStream contents, List<String> errors) throws IOException;
}
