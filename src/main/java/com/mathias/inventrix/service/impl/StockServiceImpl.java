package com.mathias.inventrix.service.impl;

import com.mathias.inventrix.domain.entity.Location;
import com.mathias.inventrix.domain.entity.PersonEntity;
import com.mathias.inventrix.domain.entity.StockSaleHistory;
import com.mathias.inventrix.domain.entity.Stocks;
import com.mathias.inventrix.domain.enums.Category;
import com.mathias.inventrix.exceptions.NotFoundException;
import com.mathias.inventrix.exceptions.StockNotAvailableException;
import com.mathias.inventrix.payload.request.CreateStockRequest;
import com.mathias.inventrix.payload.request.EditStockRequestDto;
import com.mathias.inventrix.payload.request.LocationRequest;
import com.mathias.inventrix.payload.request.SellStockDto;
import com.mathias.inventrix.payload.response.EmployeeResponse;
import com.mathias.inventrix.payload.response.StockHistoryDto;
import com.mathias.inventrix.payload.response.StockResponse;
import com.mathias.inventrix.payload.response.StockResponseDto;
import com.mathias.inventrix.repository.LocationRepository;
import com.mathias.inventrix.repository.PersonRepository;
import com.mathias.inventrix.repository.StockSaleHistoryRepository;
import com.mathias.inventrix.repository.StocksRepository;
import com.mathias.inventrix.service.StockService;
import com.mathias.inventrix.utils.StockUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class StockServiceImpl implements StockService {

    private final LocationRepository locationRepository;
    private final PersonRepository personRepository;
    private final StocksRepository stocksRepository;
    private final StockUtil stockUtil;
    private final StockSaleHistoryRepository historyRepository;

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

       PersonEntity user = personRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found"));

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
                .companyId(user.getCompanyId()) // Assign the companyId from the user
                .stkUnitNo(StockUtil.generateStockUnitNo())
                .locations(locations)
                .build();
        Stocks savedStocks = stocksRepository.save(stock);

        return StockResponse.builder()
                .responseCode("007")
                .responseMessage(savedStocks.getName() + " has been saved successfully")
                .build();
    }

    @Override
    public List<StockResponseDto> viewAllStock(String email) {
        // Fetch the logged-in user
        PersonEntity user = personRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

       List<Stocks> stocks = stocksRepository.findByCompanyId(user.getCompanyId());

        // Convert each stock entity to a StockResponseDto and return as a list
        return stocks.stream().map(stock -> StockResponseDto.builder()
                .stkUnitNo(stock.getStkUnitNo())
                .name(stock.getName())
                .price(stock.getPrice())
                .quantity(stock.getQuantity())
                .description(stock.getDescription())
                .category(stock.getCategory())
                .location(stock.getLocations() != null && !stock.getLocations().isEmpty()
                        ? stock.getLocations().iterator().next().getLocationName() // Assuming Location has getName()
                        : "No location assigned")
                .build()).collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse editStock(String email, Long stockId, EditStockRequestDto editStockRequestDto) {
        // Fetch the logged-in user
       personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

       Stocks stocks = stocksRepository.findById(stockId).orElseThrow(()-> new NotFoundException("Stock not found"));

       stocks.setName(editStockRequestDto.getName());
       stocks.setPrice(editStockRequestDto.getPrice());
       stocks.setDescription(editStockRequestDto.getDescription());
       stocks.setQuantity(editStockRequestDto.getQuantity());
       stocks.setCategory(editStockRequestDto.getCategory());

       stocksRepository.save(stocks);

        return EmployeeResponse.builder()
                .responseCode("007")
                .responseMessage("The Stock has been Updated Successfully")
                .build();
    }

    @Override
    public String deleteStock(String email, Long stockId) {
        // Fetch the logged-in user
        personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        stocksRepository.deleteById(stockId);

        return " Stock has been deleted successfully";
    }

    @Override
    public EmployeeResponse sellStock(String email, SellStockDto sellStockDto) {

        // Fetch the logged-in user
       PersonEntity user = personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        // Find the stock
        Stocks stock = stocksRepository.findById(sellStockDto.getStockId())
                .orElseThrow(() -> new NotFoundException("Stock not found"));

        // Ensure the stock belongs to the user's company
        if (!stock.getCompanyId().equals(user.getCompanyId())) {
            throw new RuntimeException("You can only sell stocks from your company!");
        }

        // Find the location
        Location location = locationRepository.findById(sellStockDto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // Check if the stock is available in this location
        if (!stock.getLocations().contains(location)) {
            throw new RuntimeException("Stock is not available at the selected location");
        }

        // Ensure enough stock is available to sell
        if (stock.getQuantity() < sellStockDto.getQuantitySold()) {
            throw new StockNotAvailableException("Insufficient quantity of '" + stock.getName() + "' in stock");
        }

        // Reduce stock quantity
        stock.setQuantity(stock.getQuantity() - sellStockDto.getQuantitySold());

        // Save updated stock
        stocksRepository.save(stock);

        StockSaleHistory saleHistory= StockSaleHistory.builder()
                .stock(stock)
                .quantitySold(sellStockDto.getQuantitySold())
                .location(location)
                .saleDate(LocalDate.now())
                .companyId(user.getCompanyId())
                .build();

        historyRepository.save(saleHistory);

        return EmployeeResponse.builder()
                .responseCode("008")
                .responseMessage("Successfully sold " + sellStockDto.getQuantitySold() + " units of '" + stock.getName())
                .build();
    }

    @Override
    public List<StockResponseDto> getStocksByCategory(String email, Category category) {
        // Fetch the logged-in user
        personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<Stocks> stocks = stocksRepository.findByCategory(category);

        return stocks.stream().map(stock -> StockResponseDto.builder()
                .stkUnitNo(stock.getStkUnitNo())
                .name(stock.getName())
                .price(stock.getPrice())
                .quantity(stock.getQuantity())
                .description(stock.getDescription())
                .category(stock.getCategory())
                .location(stock.getLocations().toString())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<StockResponseDto> getStocksByLocation(String email, Long locationId) {
        // Fetch the logged-in user
        personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException("Location not found"));

        List<Stocks> stocks = stocksRepository.findByLocationsContaining(location);


        return stocks.stream().map(stock -> StockResponseDto.builder()
                .name(stock.getName())
                .price(stock.getPrice())
                .quantity(stock.getQuantity())
                .description(stock.getDescription())
                .category(stock.getCategory())
                .location(stock.getLocations().toString())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<StockResponseDto> getStocksByName(String email, String name) {
        // Fetch the logged-in user
        personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

         List<Stocks> stocks = stocksRepository.findByNameContainingIgnoreCase(name);

        if (stocks.isEmpty()) {
            throw new StockNotAvailableException("No stock found with name: " + name);
        }

        return stocks.stream()
                .map(stock -> StockResponseDto.builder()
                        .name(stock.getName())
                        .price(stock.getPrice())
                        .quantity(stock.getQuantity())
                        .description(stock.getDescription())
                        .category(stock.getCategory())
                        .location(stock.getLocations().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public StockResponseDto getStockByStkNo(String email, String stkNo) {
        // Fetch the logged-in user
        personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

       Stocks stock = stocksRepository.findByStkUnitNoContainingIgnoreCase(stkNo);


        return StockResponseDto.builder()
                .name(stock.getName())
                .price(stock.getPrice())
                .quantity(stock.getQuantity())
                .description(stock.getDescription())
                .category(stock.getCategory())
                .location(stock.getLocations().toString())
                .build();
    }

    @Override
    public List<StockHistoryDto> getStockHistory(String email) {
        // Fetch the logged-in user
        PersonEntity user = personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        List<StockSaleHistory> saleHistories = historyRepository.findByCompanyId(user.getCompanyId());

        return saleHistories.stream().map(history ->
                StockHistoryDto.builder()
                        .stockName(history.getStock().getName())
                        .stkUnitNo(history.getStock().getStkUnitNo())
                        .locationName(history.getLocation().getLocationName())
                        .quantitySold(history.getQuantitySold())
                        .saleDate(history.getSaleDate())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<StockHistoryDto> getSalesHistory(String email, Integer day, Integer month, Integer year) {
        PersonEntity user = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String companyId = user.getCompanyId();

        // Initialize date filters
        LocalDate start = null;
        LocalDate end = null;

        if (day != null && month != null && year != null) {
            // Filter by exact date
            start = LocalDate.of(year, month, day);
            end = start;  // Since it's a single day, start and end are the same
        } else if (month != null && year != null) {
            // Filter by entire month
            start = LocalDate.of(year, month, 1);
            end = start.plusMonths(1).minusDays(1); // Last day of the month
        } else if (year != null) {
            // Filter by entire year
            start = LocalDate.of(year, 1, 1);
            end = LocalDate.of(year, 12, 31); // Last day of the year
        } else {
            throw new IllegalArgumentException("At least a year must be provided for filtering.");
        }

        // Fetch sales history with the new LocalDate filter
        List<StockSaleHistory> sales = historyRepository.findByCompanyIdAndSoldAtBetween(companyId, start, end);

        return sales.stream().map(history ->
              StockHistoryDto.builder()
                      .stockName(history.getStock().getName())
                      .locationName(history.getLocation().getLocationName())
                      .quantitySold(history.getQuantitySold())
                      .saleDate(history.getSaleDate())
                      .build()
                ).collect(Collectors.toList());
    }


}
