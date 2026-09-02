package com.enviro.assessment.junior.mpho.dto;

import jakarta.validation.constraints.DecimalMin;
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

    public WithdrawalRequest() {
    }

    public WithdrawalRequest(Long investorId, Long productId, BigDecimal amount) {
        this.investorId = investorId;
        this.productId = productId;
        this.amount = amount;
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
}
