package com.enviro365.withdrawals.repository;

import com.enviro365.withdrawals.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorRepository extends JpaRepository<Investor, Long> {
}
