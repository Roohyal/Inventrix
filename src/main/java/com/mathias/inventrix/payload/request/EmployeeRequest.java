package com.mathias.inventrix.payload.request;

import com.mathias.inventrix.domain.enums.Position;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "Your Fullname is required")
    private String fullName;

    @NotBlank(message = "Your email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "PhoneNumber is required")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Position position;

    @NotBlank(message = "The Location is needed")
    private Long locationId;
}
