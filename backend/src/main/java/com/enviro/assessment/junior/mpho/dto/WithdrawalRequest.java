package com.enviro.assessment.junior.mpho.dto;

import com.enviro.assessment.junior.mpho.entity.WithdrawalReason;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request body for creating a withdrawal notice from a specific investor product.
 * Validation guarantees a real investor/product and a positive amount before processing.
 */
public class WithdrawalRequest {

    @NotNull(message = "Investor ID is required")
    private Long investorId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Reason is required")
    private WithdrawalReason reason;

    @NotBlank(message = "Reference is required")
    private String reference;

    public WithdrawalRequest() {
    }

    public WithdrawalRequest(Long investorId, Long productId, BigDecimal amount, WithdrawalReason reason, String reference) {
        this.investorId = investorId;
        this.productId = productId;
        this.amount = amount;
        this.reason = reason;
        this.reference = reference;
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

    public WithdrawalReason getReason() {
        return reason;
    }

    public void setReason(WithdrawalReason reason) {
        this.reason = reason;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
