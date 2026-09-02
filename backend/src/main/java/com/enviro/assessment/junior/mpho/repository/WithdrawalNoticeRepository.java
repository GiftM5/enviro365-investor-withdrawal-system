package com.enviro.assessment.junior.mpho.repository;

import com.enviro.assessment.junior.mpho.entity.WithdrawalNotice;
import com.enviro.assessment.junior.mpho.entity.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Persistence access for withdrawal history records.
 * These notices provide the audit trail and recent activity shown in the dashboard and export pages.
 */
public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {
    List<WithdrawalNotice> findByInvestorId(Long investorId);
    List<WithdrawalNotice> findByInvestorIdAndStatus(Long investorId, WithdrawalStatus status);
}
