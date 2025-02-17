package com.mathias.inventrix.payload.response;

import com.mathias.inventrix.domain.enums.Position;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDetailsDto {

    private String fullName;

    private String email;

    private String phoneNumber;

    private Position position;

}
