package com.mathias.inventrix.service;

import com.mathias.inventrix.payload.request.CreateStockRequest;
import com.mathias.inventrix.payload.request.EditStockRequestDto;
import com.mathias.inventrix.payload.request.LocationRequest;
import com.mathias.inventrix.payload.request.SellStockDto;
import com.mathias.inventrix.payload.response.EmployeeResponse;
import com.mathias.inventrix.payload.response.StockResponse;
import com.mathias.inventrix.payload.response.StockResponseDto;

import java.util.List;

public interface StockService {

    String addLocation(String email, LocationRequest locationRequest);

    StockResponse createStock(String email, CreateStockRequest createStockRequest);

    List<StockResponseDto> viewAllStock(String email);

    EmployeeResponse editStock(String email,Long stockId, EditStockRequestDto editStockRequestDto);

    String deleteStock(String email, Long stockId);

    EmployeeResponse sellStock(String email, SellStockDto sellStockDto);

}
