package com.mathias.inventrix.payload.response;

import com.mathias.inventrix.domain.enums.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponseDto {

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

    private String Location;
}
