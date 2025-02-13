package com.mathias.inventrix.domain.entity;

import com.mathias.inventrix.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stocks extends BaseClass {

    private String name;

    private Long price;

    private Long quantity;

    private String description;

    @Enumerated(EnumType.STRING) // If Category is an enum
    private Category category;

    private String stkUnitNo;

    @ManyToMany
    @JoinTable(
            name = "stock_location",
            joinColumns = @JoinColumn(name = "stock_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Location> locations; // Add this missing field
}
