package com.mathias.inventrix.service.impl;

import com.mathias.inventrix.domain.entity.Location;
import com.mathias.inventrix.exceptions.NotFoundException;
import com.mathias.inventrix.payload.request.LocationRequest;
import com.mathias.inventrix.repository.LocationRepository;
import com.mathias.inventrix.repository.PersonRepository;
import com.mathias.inventrix.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class StockServiceImpl implements StockService {

    private final LocationRepository locationRepository;
    private final PersonRepository personRepository;


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
}
