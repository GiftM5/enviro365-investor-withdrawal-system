package com.enviro.assessment.junior.mpho.repository;

import com.enviro.assessment.junior.mpho.entity.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for individual investment products.
 * Balance updates during withdrawals are performed through this repository.
 */
public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long> {
}
