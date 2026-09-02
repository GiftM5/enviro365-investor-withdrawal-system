package com.enviro365.withdrawals.repository;

import com.enviro365.withdrawals.entity.WithdrawalNotice;
import com.enviro365.withdrawals.entity.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {
    List<WithdrawalNotice> findByInvestorId(Long investorId);
    List<WithdrawalNotice> findByInvestorIdAndStatus(Long investorId, WithdrawalStatus status);
}
