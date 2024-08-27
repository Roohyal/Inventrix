package com.mathias.inventrix.service.impl;


import com.mathias.inventrix.Infrastructure.config.JwtService;
import com.mathias.inventrix.domain.entity.ConfirmationTokenModel;
import com.mathias.inventrix.domain.entity.JToken;
import com.mathias.inventrix.domain.entity.PersonEntity;
import com.mathias.inventrix.domain.enums.Position;
import com.mathias.inventrix.domain.enums.Role;
import com.mathias.inventrix.domain.enums.TokenType;
import com.mathias.inventrix.exceptions.AlreadyExistException;
import com.mathias.inventrix.exceptions.NotEnabledException;
import com.mathias.inventrix.exceptions.NotFoundException;
import com.mathias.inventrix.payload.request.EmailDetails;
import com.mathias.inventrix.payload.request.LoginRequest;
import com.mathias.inventrix.payload.request.PersonRegisterRequest;
import com.mathias.inventrix.payload.response.LoginInfo;
import com.mathias.inventrix.payload.response.LoginResponse;
import com.mathias.inventrix.payload.response.PersonRegisterResponse;
import com.mathias.inventrix.repository.ConfirmationTokenRepository;
import com.mathias.inventrix.repository.JTokenRepository;
import com.mathias.inventrix.repository.PersonRepository;
import com.mathias.inventrix.service.EmailService;
import com.mathias.inventrix.service.PersonService;
import com.mathias.inventrix.utils.AccountUtil;
import com.mathias.inventrix.utils.EmailUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JTokenRepository jTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final AccountUtil accountUtil;

    @Override
    public PersonRegisterResponse registerPerson(PersonRegisterRequest registerRequest) throws MessagingException {
        // Validate email format
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(registerRequest.getEmail());

        if (!matcher.matches()) {
            throw new MessagingException("Invalid email domain");
        }
        String[] emailParts = registerRequest.getEmail().split("\\.");
        if (emailParts.length < 2 || emailParts[emailParts.length - 1].length() < 2){
            throw new MessagingException("Invalid email domain");
        }
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){
            throw new MessagingException("Passwords do not match");
        }

        Optional<PersonEntity> existingUser = personRepository.findByEmail(registerRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new AlreadyExistException("User already exists, please Login");
        }
           PersonEntity person = PersonEntity.builder()
                   .fullName(registerRequest.getFullName())
                   .email(registerRequest.getEmail())
                   .position(Position.CEO)
                   .role(Role.ADMIN)
                   .companyName(registerRequest.getCompanyName())
                   .companyId(accountUtil.generateRandomId())
                   .phoneNumber(registerRequest.getPhoneNumber())
                   .password(passwordEncoder.encode(registerRequest.getPassword()))
                   .build();

        PersonEntity savedPerson = personRepository.save(person);

        ConfirmationTokenModel confirmationTokenModel = new ConfirmationTokenModel(savedPerson);
        confirmationTokenRepository.save(confirmationTokenModel);

        String confirmationUrl = EmailUtil.getVerificationUrl(confirmationTokenModel.getToken());

        EmailDetails emailDetails = EmailDetails.builder()
                .fullname(savedPerson.getFullName())
                .companyName(savedPerson.getCompanyName())
                .companyId(savedPerson.getCompanyId())
                .recipient(savedPerson.getEmail())
                .subject("INVENTRIX ACCOUNT CREATED SUCCESSFULLY")
                .link(confirmationUrl)
                .build();

        emailService.sendEmailAlert(emailDetails,"email_verification");

        return PersonRegisterResponse.builder()
                .responseCode("001")
                .responseMessage("Your Account has been created successfully, Kindly Check your Email")
                .build();
    }


    private void saveUserToken(PersonEntity person, String jwtToken){
       var token = JToken.builder()
               .person(person)
               .token(jwtToken)
               .tokenType(TokenType.BEARER)
               .expired(false)
               .revoked(false)
               .build();
       jTokenRepository.save(token);
    }
    private void revokeAllUserTokens(PersonEntity person){
        var validUserTokens = jTokenRepository.findAllValidTokenByUser(person.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jTokenRepository.saveAll(validUserTokens);
    }


    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) throws MessagingException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        PersonEntity person = personRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new NotFoundException("User is not found"));

        if (!person.isEnabled()){
            throw new NotEnabledException("User account is not enabled. Please check your email to confirm your account.");
        }

        var jwtToken = jwtService.generateToken(person);
        revokeAllUserTokens(person);
        saveUserToken(person, jwtToken);

        return LoginResponse.builder()
                .responseCode("002")
                .responseMessage("Your have been login successfully")
                .loginInfo(LoginInfo.builder()
                        .email(person.getEmail())
                        .token(jwtToken)
                        .build())
                .build();
    }

}
