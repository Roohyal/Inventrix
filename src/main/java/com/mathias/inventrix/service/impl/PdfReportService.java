package com.mathias.inventrix.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.mathias.inventrix.domain.entity.PersonEntity;
import com.mathias.inventrix.payload.response.StockHistoryDto;
import com.mathias.inventrix.repository.PersonRepository;
import com.mathias.inventrix.repository.StockSaleHistoryRepository;
import com.mathias.inventrix.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final PersonRepository personRepository;
    private final StockSaleHistoryRepository historyRepository;
    private final StockService stockService;

    public byte[] generateSalesReport(String email, Integer day, Integer month, Integer year) {

        PersonEntity user = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<StockHistoryDto> sales = stockService.getSalesHistory(email, day, month, year);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Sales History Report").setBold().setFontSize(18));
        document.add(new Paragraph("Company: " + user.getCompanyName()));
        document.add(new Paragraph("Date: " + LocalDate.now()));

        Table table = new Table(new float[]{3, 3, 2, 3});
        table.addHeaderCell("Stock Name");
        table.addHeaderCell("Location");
        table.addHeaderCell("Quantity Sold");
        table.addHeaderCell("Sale Date");

        for (StockHistoryDto sale : sales) {
            table.addCell(sale.getStockName());
            table.addCell(sale.getLocationName());
            table.addCell(String.valueOf(sale.getQuantitySold()));
            table.addCell(sale.getSaleDate().toString());
        }
        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

}
