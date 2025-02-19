package com.mathias.inventrix.Infrastructure.controller;

import com.mathias.inventrix.payload.request.CreateStockRequest;
import com.mathias.inventrix.payload.response.StockResponse;
import com.mathias.inventrix.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-stocks")
    public ResponseEntity<?> createStocks(@RequestBody CreateStockRequest createStockRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        StockResponse response = stockService.createStock(currentUsername, createStockRequest);
        return ResponseEntity.ok(response);
    }


}
