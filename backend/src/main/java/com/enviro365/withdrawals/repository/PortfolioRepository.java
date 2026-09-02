package com.enviro365.withdrawals.repository;

import com.enviro365.withdrawals.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
