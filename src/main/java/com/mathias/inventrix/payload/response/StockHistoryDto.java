package com.mathias.inventrix.payload.response;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockHistoryDto {

    private String stkUnitNo;

    private String stockName;

    private Long quantitySold;

    private LocalDateTime saleDate;

    private String locationName;
}
