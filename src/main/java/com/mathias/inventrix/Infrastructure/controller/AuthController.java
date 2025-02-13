package com.mathias.inventrix.Infrastructure.controller;

import com.mathias.inventrix.payload.request.*;
import com.mathias.inventrix.payload.response.LoginResponse;
import com.mathias.inventrix.payload.response.PersonRegisterResponse;
import com.mathias.inventrix.service.PersonService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
     private final PersonService personService;

     @PostMapping("/register")
    public ResponseEntity<PersonRegisterResponse> registerUser(@Valid @RequestBody PersonRegisterRequest registerRequest) {

         try {
             PersonRegisterResponse registerUser = personService.registerPerson(registerRequest);
             if(!registerUser.equals("Invalid Email domain")){
                 return ResponseEntity.ok(registerUser);
             }else {
                 return ResponseEntity.badRequest().body(registerUser);
             }
         } catch (MessagingException e) {
             throw new RuntimeException(e);
         }
     }

     @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) throws MessagingException {
         return ResponseEntity.ok(personService.loginUser(loginRequest));

     }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgetPasswordRequestDto requestDto) throws MessagingException {

        String response = personService.forgotPassword(requestDto);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
        String response = personService.resetPassword(requestDto);

        return ResponseEntity.ok(response);
    }


}
