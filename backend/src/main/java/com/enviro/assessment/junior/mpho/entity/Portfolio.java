package com.enviro.assessment.junior.mpho.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Portfolio entity representing a single investor's collection of products.
 * The portfolio is the boundary used to validate product ownership during withdrawals.
 */
@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String portfolioNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false)
    private Investor investor;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvestmentProduct> products = new ArrayList<>();

    public Portfolio() {
    }

    public Portfolio(String portfolioNumber, Investor investor) {
        this.portfolioNumber = portfolioNumber;
        this.investor = investor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(String portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    public List<InvestmentProduct> getProducts() {
        return products;
    }

    public void setProducts(List<InvestmentProduct> products) {
        this.products = products;
    }
}
