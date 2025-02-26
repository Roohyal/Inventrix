package com.mathias.inventrix.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Location extends BaseClass{

    private String locationName;

    private String locationAddress;

    @ManyToMany(mappedBy = "locations")
    private Set<Stocks> stocks = new HashSet<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<PersonEntity> employees = new ArrayList<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<StockSaleHistory> stockSaleHistories = new ArrayList<>();

}
