package com.mathias.inventrix.payload.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellStockDto {

    private Long stockId;

    private Long locationId;

    private Long quantitySold;
}
