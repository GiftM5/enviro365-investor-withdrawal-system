package com.enviro.assessment.junior.mpho.repository;

import com.enviro.assessment.junior.mpho.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for investors.
 * This repository supports lookup and listing of investor records used by the portal and service layer.
 */
public interface InvestorRepository extends JpaRepository<Investor, Long> {
}
