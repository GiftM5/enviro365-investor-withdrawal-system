package com.enviro.assessment.junior.mpho;

import com.enviro.assessment.junior.mpho.dto.WithdrawalRequest;
import com.enviro.assessment.junior.mpho.dto.WithdrawalResponse;
import com.enviro.assessment.junior.mpho.entity.InvestmentProduct;
import com.enviro.assessment.junior.mpho.entity.Investor;
import com.enviro.assessment.junior.mpho.entity.Portfolio;
import com.enviro.assessment.junior.mpho.entity.ProductType;
import com.enviro.assessment.junior.mpho.entity.WithdrawalReason;
import com.enviro.assessment.junior.mpho.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.mpho.repository.InvestmentProductRepository;
import com.enviro.assessment.junior.mpho.repository.InvestorRepository;
import com.enviro.assessment.junior.mpho.repository.PortfolioRepository;
import com.enviro.assessment.junior.mpho.service.WithdrawalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(WithdrawalService.class)
class WithdrawalServiceTest {

    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private InvestmentProductRepository investmentProductRepository;

    @Test
    void shouldRejectRetirementWithdrawalForAgeSixtyFour() {
        Investor investor = createInvestor("Anne", "Four", LocalDate.now().minusYears(64).minusDays(1), "age64@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("200000.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("50000.00"), WithdrawalReason.OTHER, "REF-TEST")));

        assertThat(exception.getMessage()).contains("older than 65");
    }

    @Test
    void shouldRejectRetirementWithdrawalForAgeSixtyFive() {
        Investor investor = createInvestor("Anne", "Five", LocalDate.now().minusYears(65), "age65@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("200000.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("50000.00"), WithdrawalReason.OTHER, "REF-TEST")));

        assertThat(exception.getMessage()).contains("older than 65");
    }

    @Test
    void shouldAllowRetirementWithdrawalForAgeSixtySix() {
        Investor investor = createInvestor("Anne", "Six", LocalDate.now().minusYears(66), "age66@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("200000.00"));

        WithdrawalResponse response = withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("50000.00"), WithdrawalReason.OTHER, "REF-TEST"));

        assertThat(response.getRemainingBalance()).isEqualByComparingTo("150000.00");
    }

    @Test
    void shouldRejectWithdrawalAboveAvailableBalance() {
        Investor investor = createInvestor("Bob", "Balance", LocalDate.now().minusYears(70), "balance@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("100000.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("100001.00"), WithdrawalReason.OTHER, "REF-TEST")));

        assertThat(exception.getMessage()).contains("available product balance");
    }

    @Test
    void shouldAcceptWithdrawalExactlyAtNinetyPercentLimit() {
        Investor investor = createInvestor("Cara", "Limit", LocalDate.now().minusYears(70), "limit@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("100000.00"));

        WithdrawalResponse response = withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("90000.00"), WithdrawalReason.OTHER, "REF-TEST"));

        assertThat(response.getRemainingBalance()).isEqualByComparingTo("10000.00");
    }

    @Test
    void shouldRejectWithdrawalAboveNinetyPercentLimit() {
        Investor investor = createInvestor("Dane", "Limit", LocalDate.now().minusYears(70), "limit2@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("100000.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("90001.00"), WithdrawalReason.OTHER, "REF-TEST")));

        assertThat(exception.getMessage()).contains("maximum allowed");
    }

    @Test
    void shouldRejectZeroAndNegativeAmounts() {
        Investor investor = createInvestor("Eve", "Positive", LocalDate.now().minusYears(70), "positive@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("100000.00"));

        assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), BigDecimal.ZERO, WithdrawalReason.OTHER, "REF-TEST")));
        assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("-100.00"), WithdrawalReason.OTHER, "REF-TEST")));
    }

    @Test
    void shouldUpdateBalanceAndPersistRemainingBalanceOnApproval() {
        Investor investor = createInvestor("Frank", "Balance", LocalDate.now().minusYears(70), "balance2@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("200000.00"));

        WithdrawalResponse response = withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("50000.00"), WithdrawalReason.OTHER, "REF-TEST"));

        assertThat(response.getPreviousBalance()).isEqualByComparingTo("200000.00");
        assertThat(response.getRemainingBalance()).isEqualByComparingTo("150000.00");
        assertThat(investmentProductRepository.findById(product.getId()).orElseThrow().getBalance())
                .isEqualByComparingTo("150000.00");
    }

    @Test
    void shouldExposeInvestorAgeOnPortfolioResponse() {
        LocalDate dateOfBirth = LocalDate.now().minusYears(42).minusDays(10);
        Investor investor = createInvestor("Grace", "Age", dateOfBirth, "age@example.com");
        createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("200000.00"));

        var response = withdrawalService.getPortfolio(investor.getId());

        assertThat(response.getAge()).isEqualTo(Period.between(dateOfBirth, LocalDate.now()).getYears());
    }

    @Test
    void shouldRejectWithdrawalWhenProductDoesNotBelongToInvestor() {
        Investor investor = createInvestor("Grace", "Owner", LocalDate.now().minusYears(70), "owner@example.com");
        Investor otherInvestor = createInvestor("Hank", "Other", LocalDate.now().minusYears(72), "other@example.com");
        InvestmentProduct product = createRetirementProduct(otherInvestor, "Retirement Annuity", new BigDecimal("200000.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("10000.00"), WithdrawalReason.OTHER, "REF-TEST")));

        assertThat(exception.getMessage()).contains("does not belong to this investor");
    }

    @Test
    void shouldNotChangeBalanceWhenWithdrawalIsRejected() {
        Investor investor = createInvestor("Ivy", "Reject", LocalDate.now().minusYears(70), "reject@example.com");
        InvestmentProduct product = createRetirementProduct(investor, "Retirement Annuity", new BigDecimal("100000.00"));

        assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("90001.00"), WithdrawalReason.OTHER, "REF-TEST")));

        assertThat(investmentProductRepository.findById(product.getId()).orElseThrow().getBalance())
                .isEqualByComparingTo("100000.00");
    }

    @Test
    void shouldAllowInvestmentProductWithdrawalWithoutAgeRestriction() {
        Investor investor = createInvestor("Jill", "Investment", LocalDate.now().minusYears(60), "investment@example.com");
        InvestmentProduct product = createProduct(investor, "Growth Fund", ProductType.INVESTMENT, new BigDecimal("150000.00"));

        WithdrawalResponse response = withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), product.getId(), new BigDecimal("60000.00"), WithdrawalReason.OTHER, "REF-TEST"));

        assertThat(response.getRemainingBalance()).isEqualByComparingTo("90000.00");
    }

    @Test
    void shouldRejectWhenInvestorDoesNotExist() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(999999L, 1L, new BigDecimal("1000.00"), WithdrawalReason.OTHER, "REF-TEST")));

        assertThat(exception.getMessage()).contains("Investor not found");
    }

    @Test
    void shouldRejectWhenProductDoesNotExist() {
        Investor investor = createInvestor("Kim", "MissingProduct", LocalDate.now().minusYears(70), "missing-product@example.com");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> withdrawalService.createWithdrawal(new WithdrawalRequest(investor.getId(), 999999L, new BigDecimal("1000.00"), WithdrawalReason.OTHER, "REF-TEST")));

        assertThat(exception.getMessage()).contains("Product not found");
    }

    private Investor createInvestor(String firstName, String lastName, LocalDate dateOfBirth, String email) {
        Investor investor = new Investor(firstName, lastName, dateOfBirth, email);
        return investorRepository.save(investor);
    }

    private InvestmentProduct createRetirementProduct(Investor investor, String name, BigDecimal balance) {
        return createProduct(investor, name, ProductType.RETIREMENT, balance);
    }

    private InvestmentProduct createProduct(Investor investor, String name, ProductType productType, BigDecimal balance) {
        Portfolio portfolio = portfolioRepository.save(new Portfolio("ENV-" + investor.getId() + "-" + System.currentTimeMillis(), investor));
        return investmentProductRepository.save(new InvestmentProduct(name, productType, balance, portfolio));
    }
}
