package com.mathias.inventrix.service;

import com.mathias.inventrix.payload.request.CreateStockRequest;
import com.mathias.inventrix.payload.request.LocationRequest;
import com.mathias.inventrix.payload.response.StockResponse;

public interface StockService {

    String addLocation(String email, LocationRequest locationRequest);

    StockResponse createStock(String email, CreateStockRequest createStockRequest);

}
