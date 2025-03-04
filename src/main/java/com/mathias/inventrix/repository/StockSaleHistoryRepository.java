package com.mathias.inventrix.repository;

import com.mathias.inventrix.domain.entity.StockSaleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockSaleHistoryRepository extends JpaRepository<StockSaleHistory, Long> {

    List<StockSaleHistory> findByCompanyId(String companyId);

    List<StockSaleHistory> findByCompanyIdAndSaleDate(String companyId, LocalDate date);

}
