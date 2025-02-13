package com.mathias.inventrix.service.impl;


import com.mathias.inventrix.Infrastructure.config.JwtService;
import com.mathias.inventrix.domain.entity.ConfirmationTokenModel;
import com.mathias.inventrix.domain.entity.JToken;
import com.mathias.inventrix.domain.entity.Location;
import com.mathias.inventrix.domain.entity.PersonEntity;
import com.mathias.inventrix.domain.enums.Position;
import com.mathias.inventrix.domain.enums.Role;
import com.mathias.inventrix.domain.enums.TokenType;
import com.mathias.inventrix.exceptions.AlreadyExistException;
import com.mathias.inventrix.exceptions.NotEnabledException;
import com.mathias.inventrix.exceptions.NotFoundException;
import com.mathias.inventrix.payload.request.*;
import com.mathias.inventrix.payload.response.EmployeeResponse;
import com.mathias.inventrix.payload.response.LoginInfo;
import com.mathias.inventrix.payload.response.LoginResponse;
import com.mathias.inventrix.payload.response.PersonRegisterResponse;
import com.mathias.inventrix.repository.ConfirmationTokenRepository;
import com.mathias.inventrix.repository.JTokenRepository;
import com.mathias.inventrix.repository.LocationRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private final EmailUtil emailUtil;
    private final LocationRepository locationRepository;

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

        String confirmationUrl = emailUtil.getVerificationUrl(confirmationTokenModel.getToken());

        EmailDetails emailDetails = EmailDetails.builder()
                .fullName(savedPerson.getFullName())
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

    @Override
    public String forgotPassword(ForgetPasswordRequestDto forgetpassword) throws MessagingException {
        PersonEntity person = personRepository.findByEmail(forgetpassword.getEmail())
                .orElseThrow(()-> new NotFoundException("User is not found"));

        String token = UUID.randomUUID().toString();
        person.setResetToken(token);
        person.setResetTokenCreationTime(LocalDateTime.now());
        personRepository.save(person);

        String resetUrl = emailUtil.getResetUrl(token);

        EmailDetails emailDetails = EmailDetails.builder()
                .fullName(person.getFullName())
                .recipient(person.getEmail())
                .subject("INVENTRIX!!! RESET YOUR PASSWORD ")
                .link(resetUrl)
                .build();

        emailService.sendEmailAlert(emailDetails,"forgot_password");


        return "A reset password link has been sent to your account email address";
    }

    @Override
    public String resetPassword(ResetPasswordRequestDto resetpassword) {

        PersonEntity person =  personRepository.findByResetToken(resetpassword.getToken()).orElseThrow(()-> new NotFoundException("User is not found"));

        if (Duration.between(person.getResetTokenCreationTime(), LocalDateTime.now()).toMinutes() > 5) {
            person.setResetToken(null);
            personRepository.save(person);
            throw new NotEnabledException("Token has expired!");
        }
        if(!resetpassword.getPassword().equals(resetpassword.getConfirmPassword())){
            throw new NotEnabledException("Confirmation Password does not match!");
        }

        person.setPassword(passwordEncoder.encode(resetpassword.getPassword()));

        // set the reset token to null
        person.setResetToken(null);

        personRepository.save(person);

        return "Password Reset is Successful";
    }

    @Override
    public EmployeeResponse addEmployee(String email, EmployeeRequest employeeRequest) throws MessagingException {
        personRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User is not found"));

        // Get the logged-in Admin from the security context
        PersonEntity admin = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Ensure only Admins can create Employees
        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only Admins can create Employees");
        }

        // Validate email format
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(employeeRequest.getEmail());

        if (!matcher.matches()) {
            throw new MessagingException("Invalid email domain");
        }
        String[] emailParts = employeeRequest.getEmail().split("\\.");
        if (emailParts.length < 2 || emailParts[emailParts.length - 1].length() < 2){
            throw new MessagingException("Invalid email domain");
        }

        // Find the location (mandatory for Employees)
        Location location = locationRepository.findById(employeeRequest.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // Generate and encode password
        String rawPassword = generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Create the new Employee
        PersonEntity employee = PersonEntity.builder()
                .fullName(employeeRequest.getFullName())
                .email(employeeRequest.getEmail())
                .location(location)
                .password(encodedPassword)
                .role(Role.USER)
                .phoneNumber(employeeRequest.getPhoneNumber())
                .companyName(admin.getCompanyName())
                .companyId(admin.getCompanyId())
                .position(employeeRequest.getPosition())
                .createdByAdmin(admin)
                .build();

        PersonEntity savedEmployee = personRepository.save(employee);

        ConfirmationTokenModel confirmationTokenModel = new ConfirmationTokenModel(savedEmployee);
        confirmationTokenRepository.save(confirmationTokenModel);

        String confirmationUrl = emailUtil.getVerificationUrl(confirmationTokenModel.getToken());

        EmailDetails emailDetails = EmailDetails.builder()
                .fullName(savedEmployee.getFullName())
                .companyName(savedEmployee.getCompanyName())
                .recipient(savedEmployee.getEmail())
                .password(savedEmployee.getPassword())
                .subject("INVENTRIX ACCOUNT CREATED SUCCESSFULLY")
                .link(confirmationUrl)
                .build();

        emailService.sendEmailAlert(emailDetails,"email_verification");

        return EmployeeResponse.builder()
                .responseCode("005")
                .responseMessage("Your Employee's account has been created successfully, they should kindly check their email")
                .build();
    }

    // Generate an 8-character random password
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
