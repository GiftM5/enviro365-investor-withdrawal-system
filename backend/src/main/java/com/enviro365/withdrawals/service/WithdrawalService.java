package com.enviro365.withdrawals.service;

import com.enviro365.withdrawals.dto.PortfolioResponse;
import com.enviro365.withdrawals.dto.WithdrawalRequest;
import com.enviro365.withdrawals.dto.WithdrawalResponse;
import com.enviro365.withdrawals.entity.*;
import com.enviro365.withdrawals.exception.ResourceNotFoundException;
import com.enviro365.withdrawals.repository.InvestmentProductRepository;
import com.enviro365.withdrawals.repository.InvestorRepository;
import com.enviro365.withdrawals.repository.WithdrawalNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WithdrawalService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final InvestorRepository investorRepository;
    private final InvestmentProductRepository investmentProductRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public WithdrawalService(InvestorRepository investorRepository,
                            InvestmentProductRepository investmentProductRepository,
                            WithdrawalNoticeRepository withdrawalNoticeRepository) {
        this.investorRepository = investorRepository;
        this.investmentProductRepository = investmentProductRepository;
        this.withdrawalNoticeRepository = withdrawalNoticeRepository;
    }

    public PortfolioResponse getPortfolio(Long investorId) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with id: " + investorId));

        PortfolioResponse response = new PortfolioResponse();
        response.setInvestorId(investor.getId());
        response.setInvestorName(investor.getFullName());

        if (investor.getPortfolios() != null && !investor.getPortfolios().isEmpty()) {
            Portfolio portfolio = investor.getPortfolios().getFirst();
            response.setPortfolioNumber(portfolio.getPortfolioNumber());

            List<PortfolioResponse.ProductSummary> products = new ArrayList<>();
            for (InvestmentProduct product : portfolio.getProducts()) {
                BigDecimal maxWithdrawal = calculateMaximumWithdrawal(product.getProductType(), product.getBalance());
                products.add(new PortfolioResponse.ProductSummary(
                        product.getId(),
                        product.getProductName(),
                        product.getProductType().name(),
                        product.getBalance(),
                        maxWithdrawal
                ));
            }
            response.setProducts(products);
        } else {
            response.setPortfolioNumber("N/A");
            response.setProducts(new ArrayList<>());
        }

        return response;
    }

    @Transactional
    public WithdrawalResponse createWithdrawal(WithdrawalRequest request) {
        Investor investor = investorRepository.findById(request.getInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with id: " + request.getInvestorId()));

        InvestmentProduct product = investmentProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        validateOwnership(investor, product);
        validatePositiveAmount(request.getAmount());
        validateRetirementEligibility(investor, product.getProductType());
        validateWithdrawalLimits(product.getBalance(), request.getAmount());

        BigDecimal previousBalance = product.getBalance();
        BigDecimal remainingBalance = previousBalance.subtract(request.getAmount());
        product.setBalance(remainingBalance);

        WithdrawalNotice notice = new WithdrawalNotice(
                investor.getId(),
                product.getId(),
                request.getAmount(),
                previousBalance,
                remainingBalance
        );

        withdrawalNoticeRepository.save(notice);
        investmentProductRepository.save(product);

        return new WithdrawalResponse(
                notice.getId(),
                investor.getId(),
                product.getId(),
                request.getAmount(),
                previousBalance,
                remainingBalance,
                notice.getStatus(),
                notice.getCreatedAt()
        );
    }

    public List<WithdrawalNotice> getWithdrawalHistory(Long investorId) {
        investorRepository.findById(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with id: " + investorId));
        return withdrawalNoticeRepository.findByInvestorId(investorId);
    }

    private void validateOwnership(Investor investor, InvestmentProduct product) {
        boolean valid = product.getPortfolio() != null
                && product.getPortfolio().getInvestor() != null
                && product.getPortfolio().getInvestor().getId().equals(investor.getId());

        if (!valid) {
            throw new IllegalArgumentException("Withdrawal request does not belong to this investor.");
        }
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
    }

    private void validateRetirementEligibility(Investor investor, ProductType productType) {
        if (productType == ProductType.RETIREMENT) {
            int age = LocalDate.now().getYear() - investor.getDateOfBirth().getYear();
            if (investor.getDateOfBirth().plusYears(age).isAfter(LocalDate.now())) {
                age = age - 1;
            }

            if (age <= 65) {
                throw new IllegalArgumentException("Retirement withdrawals are only allowed for investors older than 65.");
            }
        }
    }

    private void validateWithdrawalLimits(BigDecimal balance, BigDecimal amount) {
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Withdrawal amount exceeds the available product balance.");
        }

        BigDecimal maxAllowed = balance.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(maxAllowed) > 0) {
            throw new IllegalArgumentException("Withdrawal amount exceeds the maximum allowed amount.");
        }
    }

    private BigDecimal calculateMaximumWithdrawal(ProductType productType, BigDecimal balance) {
        if (productType == ProductType.RETIREMENT) {
            return balance.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
        }
        return balance.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
    }
}
