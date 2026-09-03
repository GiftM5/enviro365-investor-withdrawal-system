package com.enviro.assessment.junior.mpho.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Portfolio payload returned to the investor dashboard.
 * It contains the investor identity, portfolio reference, and product summaries with limits.
 */
public class PortfolioResponse {
    private Long investorId;
    private String investorName;
    private String email;
    private String portfolioNumber;
    private LocalDate dateOfBirth;
    private Integer age;
    private List<ProductSummary> products;

    public PortfolioResponse() {
    }

    public PortfolioResponse(Long investorId, String investorName, String portfolioNumber, LocalDate dateOfBirth, Integer age, List<ProductSummary> products) {
        this.investorId = investorId;
        this.investorName = investorName;
        this.portfolioNumber = portfolioNumber;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.products = products;
    }

    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(String portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<ProductSummary> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSummary> products) {
        this.products = products;
    }

    public static class ProductSummary {
        private Long productId;
        private String productName;
        private String productType;
        private BigDecimal balance;
        private BigDecimal maximumWithdrawal;

        public ProductSummary() {
        }

        public ProductSummary(Long productId, String productName, String productType, BigDecimal balance, BigDecimal maximumWithdrawal) {
            this.productId = productId;
            this.productName = productName;
            this.productType = productType;
            this.balance = balance;
            this.maximumWithdrawal = maximumWithdrawal;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal getMaximumWithdrawal() {
            return maximumWithdrawal;
        }

        public void setMaximumWithdrawal(BigDecimal maximumWithdrawal) {
            this.maximumWithdrawal = maximumWithdrawal;
        }
    }
}
