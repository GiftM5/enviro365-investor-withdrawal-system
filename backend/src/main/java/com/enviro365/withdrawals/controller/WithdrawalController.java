package com.enviro365.withdrawals.controller;

import com.enviro365.withdrawals.dto.PortfolioResponse;
import com.enviro365.withdrawals.dto.WithdrawalRequest;
import com.enviro365.withdrawals.dto.WithdrawalResponse;
import com.enviro365.withdrawals.entity.WithdrawalNotice;
import com.enviro365.withdrawals.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @GetMapping("/investors/{investorId}/portfolio")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable Long investorId) {
        return ResponseEntity.ok(withdrawalService.getPortfolio(investorId));
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<WithdrawalResponse> createWithdrawal(@Valid @RequestBody WithdrawalRequest request) {
        WithdrawalResponse response = withdrawalService.createWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/investors/{investorId}/withdrawals")
    public ResponseEntity<List<WithdrawalNotice>> getWithdrawalHistory(@PathVariable Long investorId) {
        return ResponseEntity.ok(withdrawalService.getWithdrawalHistory(investorId));
    }
}
