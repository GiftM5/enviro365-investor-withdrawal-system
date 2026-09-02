package com.enviro365.withdrawals.repository;

import com.enviro365.withdrawals.entity.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long> {
}
