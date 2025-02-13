package com.mathias.inventrix.service;

import com.mathias.inventrix.payload.request.*;
import com.mathias.inventrix.payload.response.EmployeeResponse;
import com.mathias.inventrix.payload.response.LoginResponse;
import com.mathias.inventrix.payload.response.PersonRegisterResponse;
import jakarta.mail.MessagingException;

public interface PersonService {
    PersonRegisterResponse registerPerson(PersonRegisterRequest RegisterRequest) throws MessagingException;

    LoginResponse loginUser(LoginRequest LoginRequest) throws MessagingException;

    String forgotPassword(ForgetPasswordRequestDto forgetpassword) throws MessagingException;

    String resetPassword(ResetPasswordRequestDto resetpassword);

   EmployeeResponse addEmployee(String email, EmployeeRequest employeeRequest) throws MessagingException;

}
