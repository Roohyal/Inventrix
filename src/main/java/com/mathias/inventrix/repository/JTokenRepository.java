package com.mathias.inventrix.repository;

import com.mathias.inventrix.domain.entity.JToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JTokenRepository extends JpaRepository<JToken, Long> {

    @Query(value = """
      select t from JToken t inner join PersonEntity u\s
      on t.person.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
     List<JToken> findAllValidTokenByUser(Long id);

    Optional<JToken> findByToken(String token);
}
