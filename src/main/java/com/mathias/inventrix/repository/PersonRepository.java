package com.mathias.inventrix.repository;

import com.mathias.inventrix.domain.entity.PersonEntity;
import com.mathias.inventrix.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    Optional<PersonEntity> findByEmail(String email);

    Optional<PersonEntity> findByResetToken(String token);

    Optional<PersonEntity> findByRole(Role role);

    List<PersonEntity> findByCompanyId(String companyId);
}
