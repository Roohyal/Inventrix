package com.mathias.inventrix.Infrastructure.controller;

import com.mathias.inventrix.domain.entity.Location;
import com.mathias.inventrix.domain.enums.Category;
import com.mathias.inventrix.payload.request.CreateStockRequest;
import com.mathias.inventrix.payload.request.EditStockRequestDto;
import com.mathias.inventrix.payload.request.SellStockDto;
import com.mathias.inventrix.payload.response.EmployeeResponse;
import com.mathias.inventrix.payload.response.StockHistoryDto;
import com.mathias.inventrix.payload.response.StockResponse;
import com.mathias.inventrix.payload.response.StockResponseDto;
import com.mathias.inventrix.service.StockService;
import com.mathias.inventrix.service.impl.PdfReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;
    private final PdfReportService pdfReportService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-stocks")
    public ResponseEntity<?> createStocks(@RequestBody CreateStockRequest createStockRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        StockResponse response = stockService.createStock(currentUsername, createStockRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view-stocks")
    public ResponseEntity<?> getStocks(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
       List<StockResponseDto> response = stockService.viewAllStock(currentUsername);
       return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/edit-stocks")
    public ResponseEntity<?> editStocks (@RequestParam Long stockId,  @RequestBody EditStockRequestDto editStockRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        EmployeeResponse response = stockService.editStock(currentUsername, stockId, editStockRequestDto);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-stocks")
    public ResponseEntity<?> deleteStocks (@RequestParam Long stockId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        String response = stockService.deleteStock(currentUsername, stockId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sell-stock")
    public ResponseEntity<?> sellStock(@RequestBody SellStockDto sellStockDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        EmployeeResponse response = stockService.sellStock(currentUsername, sellStockDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-by-category")
    public ResponseEntity<?> getStocksByCategory(@RequestParam Category category){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        List<StockResponseDto> response = stockService.getStocksByCategory(currentUsername, category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-by-location")
    public ResponseEntity<?> getStocksByLocation(@RequestParam Long locationId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        List<StockResponseDto> response = stockService.getStocksByLocation(currentUsername, locationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-by-name")
    public ResponseEntity<?> getStocksByName(@RequestParam String name){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        List<StockResponseDto> response = stockService.getStocksByName(currentUsername, name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-by-stk")
    public ResponseEntity<?> getStocksByStockId(@RequestParam String stockId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        StockResponseDto response = stockService.getStockByStkNo(currentUsername,stockId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-stock-history")
    public ResponseEntity<?>getStockHistory(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        List<StockHistoryDto> response = stockService.getStockHistory(currentUsername);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sales-history")
    public ResponseEntity<?>getSalesHistory(@RequestParam(required = false) Integer day,
                                            @RequestParam(required = false) Integer month,
                                            @RequestParam(required = false) Integer year){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        List<StockHistoryDto> history = stockService.getSalesHistory(currentUsername,day,month,year);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/sales-history-pdf")
    public ResponseEntity<?> downloadSalesHistoryReport(@RequestParam(required = false) Integer day,
                                                        @RequestParam(required = false) Integer month,
                                                        @RequestParam(required = false) Integer year){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        byte[] pdfData = pdfReportService.generateSalesReport(currentUsername, day, month, year);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("Sales_Report.pdf").build());

        return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
    }

}
