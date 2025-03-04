package com.mathias.inventrix.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSaleHistory extends BaseClass {


    private Long quantitySold; // Quantity sold

    private LocalDate saleDate; // Date and time of sale

    private String companyId; // Add this field to link the sale to a company

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stocks stock; // Reference to the stock entity

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

}
