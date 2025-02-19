package com.mathias.inventrix.service;

import com.mathias.inventrix.payload.request.*;
import com.mathias.inventrix.payload.response.*;
import jakarta.mail.MessagingException;

import java.util.List;

public interface PersonService {
    PersonRegisterResponse registerPerson(PersonRegisterRequest RegisterRequest) throws MessagingException;

    LoginResponse loginUser(LoginRequest LoginRequest) throws MessagingException;

    String forgotPassword(ForgetPasswordRequestDto forgetpassword) throws MessagingException;

    String resetPassword(ResetPasswordRequestDto resetpassword);

   EmployeeResponse addEmployee(String email, EmployeeRequest employeeRequest) throws MessagingException;

   PersonDetailsDto viewUserDetails(String email);

   EditUserResponse editUser(String email, EditUserRequestDto editUserRequest);

   String deleteUser(String email, Long id);

   String makeAdmin(String email, Long id);

  List<EmployeeDetailsDto> viewEmployeeDetails(String email);


}
