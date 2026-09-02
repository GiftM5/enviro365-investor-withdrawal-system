package com.enviro.assessment.junior.mpho.dto;

import com.enviro.assessment.junior.mpho.entity.WithdrawalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response model returned after a successful withdrawal attempt.
 * It captures the resulting balance state for the UI to display immediately to the investor.
 */
public class WithdrawalResponse {
    private Long withdrawalId;
    private Long investorId;
    private Long productId;
    private BigDecimal amount;
    private BigDecimal previousBalance;
    private BigDecimal remainingBalance;
    private WithdrawalStatus status;
    private LocalDateTime createdAt;

    public WithdrawalResponse() {
    }

    public WithdrawalResponse(Long withdrawalId, Long investorId, Long productId, BigDecimal amount,
                             BigDecimal previousBalance, BigDecimal remainingBalance,
                             WithdrawalStatus status, LocalDateTime createdAt) {
        this.withdrawalId = withdrawalId;
        this.investorId = investorId;
        this.productId = productId;
        this.amount = amount;
        this.previousBalance = previousBalance;
        this.remainingBalance = remainingBalance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getWithdrawalId() {
        return withdrawalId;
    }

    public void setWithdrawalId(Long withdrawalId) {
        this.withdrawalId = withdrawalId;
    }

    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
