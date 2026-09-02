package com.enviro.assessment.junior.mpho.repository;

import com.enviro.assessment.junior.mpho.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for investor portfolios.
 * Portfolios are the grouping mechanism used when returning product data to the dashboard.
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
