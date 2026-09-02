package com.enviro.assessment.junior.mpho.controller;

import com.enviro.assessment.junior.mpho.dto.InvestorSummary;
import com.enviro.assessment.junior.mpho.dto.PortfolioResponse;
import com.enviro.assessment.junior.mpho.dto.WithdrawalRequest;
import com.enviro.assessment.junior.mpho.dto.WithdrawalResponse;
import com.enviro.assessment.junior.mpho.entity.WithdrawalNotice;
import com.enviro.assessment.junior.mpho.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST entry points for investor portfolio and withdrawal operations.
 * The controller exposes the frontend-facing API contract and delegates the business logic
 * to the withdrawal service layer.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @GetMapping("/investors")
    public ResponseEntity<List<InvestorSummary>> getInvestors() {
        return ResponseEntity.ok(withdrawalService.getAllInvestors());
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

    @GetMapping("/investors/{investorId}/withdrawals/export")
    public ResponseEntity<String> exportWithdrawalsCsv(
            @PathVariable Long investorId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status) {

        String csv = withdrawalService.exportWithdrawalsCsv(investorId, productId, from, to, status);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=withdrawal-history-" + investorId + ".csv");

        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }
}
