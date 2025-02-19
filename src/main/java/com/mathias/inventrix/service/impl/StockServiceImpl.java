package com.mathias.inventrix.service.impl;

import com.mathias.inventrix.domain.entity.Location;
import com.mathias.inventrix.domain.entity.Stocks;
import com.mathias.inventrix.exceptions.NotFoundException;
import com.mathias.inventrix.payload.request.CreateStockRequest;
import com.mathias.inventrix.payload.request.LocationRequest;
import com.mathias.inventrix.payload.response.StockResponse;
import com.mathias.inventrix.repository.LocationRepository;
import com.mathias.inventrix.repository.PersonRepository;
import com.mathias.inventrix.repository.StocksRepository;
import com.mathias.inventrix.service.StockService;
import com.mathias.inventrix.utils.StockUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class StockServiceImpl implements StockService {

    private final LocationRepository locationRepository;
    private final PersonRepository personRepository;
    private final StocksRepository stocksRepository;
    private final StockUtil stockUtil;

    @Override
    public String addLocation(String email, LocationRequest locationRequest) {

        personRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found"));

        Location location = Location.builder()
                .locationAddress(locationRequest.getAddress())
                .locationName(locationRequest.getName())
                .build();

        locationRepository.save(location);
        return "The Location has been successfully added";
    }

    @Override
    public StockResponse createStock(String email, CreateStockRequest createStockRequest) {

        personRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found"));

        Set<Location> locations = new HashSet<>(locationRepository.findAllById(createStockRequest.getLocationId()));

        if (locations.isEmpty()) {
            throw new RuntimeException("Invalid location IDs provided.");
        }

        Stocks stock = Stocks.builder()
                .name(createStockRequest.getName())
                .price(createStockRequest.getPrice())
                .description(createStockRequest.getDescription())
                .quantity(createStockRequest.getQuantity())
                .category(createStockRequest.getCategory())
                .stkUnitNo(StockUtil.generateStockUnitNo())
                .locations(locations)
                .build();
        Stocks savedStocks = stocksRepository.save(stock);

        return StockResponse.builder()
                .responseCode("007")
                .responseMessage(savedStocks.getName() + " has been saved successfully")
                .build();
    }
}
