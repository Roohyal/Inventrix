package com.mathias.inventrix.payload.request;

import com.mathias.inventrix.domain.enums.Position;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditUserRequestDto {
    private String fullName;
    private String phoneNumber;
    private Position position;
}
