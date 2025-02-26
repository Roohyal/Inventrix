package com.mathias.inventrix.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellStockDto {
    @NotNull(message = "Stock ID is required")
    private Long stockId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "Quantity sold is required")
    @Min(value = 1, message = "Quantity sold must be at least 1")
    private Long quantitySold;
}
