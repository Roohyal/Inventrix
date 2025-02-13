package com.mathias.inventrix.repository;

import com.mathias.inventrix.domain.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
