package com.mathias.inventrix.service;

import com.mathias.inventrix.payload.request.LoginRequest;
import com.mathias.inventrix.payload.request.PersonRegisterRequest;
import com.mathias.inventrix.payload.response.LoginResponse;
import com.mathias.inventrix.payload.response.PersonRegisterResponse;
import jakarta.mail.MessagingException;

public interface PersonService {
    PersonRegisterResponse registerPerson(PersonRegisterRequest RegisterRequest) throws MessagingException;

    LoginResponse loginUser(LoginRequest LoginRequest) throws MessagingException;

}
