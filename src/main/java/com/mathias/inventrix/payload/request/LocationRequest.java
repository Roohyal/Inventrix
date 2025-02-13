package com.mathias.inventrix.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationRequest {
    private String name;
    private String address;
}
