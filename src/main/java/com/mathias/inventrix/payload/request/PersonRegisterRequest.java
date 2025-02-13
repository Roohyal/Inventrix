package com.mathias.inventrix.payload.request;

import com.mathias.inventrix.domain.enums.Position;
import com.mathias.inventrix.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PersonRegisterRequest {

    @NotBlank(message = "Full name should not be null")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format ")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Input your password again")
    private String confirmPassword;

    @NotBlank(message = "Company name cannot be blank")
    private String companyName;

    private Role role;

    private Position position;

}
