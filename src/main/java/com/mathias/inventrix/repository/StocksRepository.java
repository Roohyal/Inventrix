package com.mathias.inventrix.repository;

import com.mathias.inventrix.domain.entity.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StocksRepository extends JpaRepository<Stocks, Long> {

    List<Stocks> findByCompanyId(String companyId);
}
