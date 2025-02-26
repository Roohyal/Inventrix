package com.mathias.inventrix.domain.entity;

import com.mathias.inventrix.domain.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stocks extends BaseClass {

    @NotBlank(message = "Name of the stock is required")
    private String name;

    @NotBlank(message = "Price of the stock is required")
    private Long price;

    @NotBlank(message = "Quantity of the stock is required")
    private Long quantity;

    @NotBlank(message = "Description of the stock is required")
    private String description;

    @NotBlank(message = "Category of the stock is required")
    @Enumerated(EnumType.STRING) // If Category is an enum
    private Category category;

    @NotNull(message = "UnitNo cannot be null")
    private String stkUnitNo;

    @NotBlank(message = "Company ID is required")
    private String companyId;


    @ManyToMany
    @JoinTable(
            name = "stock_location",
            joinColumns = @JoinColumn(name = "stock_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Location> locations; // Add this missing field

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockSaleHistory> saleHistories;
}
