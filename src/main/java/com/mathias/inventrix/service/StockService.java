package com.mathias.inventrix.service;

import com.mathias.inventrix.payload.request.LocationRequest;

public interface StockService {

    String addLocation(String email, LocationRequest locationRequest);

}
