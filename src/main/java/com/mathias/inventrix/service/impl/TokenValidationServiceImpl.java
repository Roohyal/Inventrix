package com.mathias.inventrix.service.impl;

import com.mathias.inventrix.domain.entity.ConfirmationTokenModel;
import com.mathias.inventrix.domain.entity.PersonEntity;
import com.mathias.inventrix.repository.ConfirmationTokenRepository;
import com.mathias.inventrix.repository.PersonRepository;
import com.mathias.inventrix.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenValidationServiceImpl implements TokenValidationService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PersonRepository personRepository;

    @Override
    public String validateToken(String token) {
        Optional<ConfirmationTokenModel> confirmationTokenOptional = confirmationTokenRepository.findByToken(token);
        if (confirmationTokenOptional.isEmpty()) {
            return "Invalid token";
        }

        ConfirmationTokenModel confirmationToken = confirmationTokenOptional.get();

        if (confirmationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token has expired";
        }
        PersonEntity user = confirmationToken.getPersons();
        user.setEnabled(true);
        personRepository.save(user);

        confirmationTokenRepository.delete(confirmationToken);

        return "Email confirmation is successful";
    }
}
