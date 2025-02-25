package com.mathias.inventrix.repository;

import com.mathias.inventrix.domain.entity.Location;
import com.mathias.inventrix.domain.entity.Stocks;
import com.mathias.inventrix.domain.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StocksRepository extends JpaRepository<Stocks, Long> {

    List<Stocks> findByCompanyId(String companyId);

    List<Stocks> findByCategory(Category category);

    List<Stocks> findByLocationsContaining(Location location);

    List<Stocks> findByNameContainingIgnoreCase(String name);

    Stocks findByStkUnitNoContainingIgnoreCase(String stkUnitNo);
}
