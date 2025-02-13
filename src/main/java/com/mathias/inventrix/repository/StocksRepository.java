package com.mathias.inventrix.repository;

import com.mathias.inventrix.domain.entity.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StocksRepository extends JpaRepository<Stocks, Long> {
}
