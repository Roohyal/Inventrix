package com.mathias.inventrix.repository;

import com.mathias.inventrix.domain.entity.ConfirmationTokenModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenModel, Long> {
    Optional<ConfirmationTokenModel> findByToken(String token);
}
