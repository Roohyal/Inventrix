package com.mathias.inventrix.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonRegisterResponse {

    private String responseCode;

    private String responseMessage;
}
