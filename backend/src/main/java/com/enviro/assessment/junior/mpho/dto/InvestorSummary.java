package com.enviro.assessment.junior.mpho.dto;

import java.time.LocalDate;

/**
 * Lightweight investor representation for directory and dashboard screens.
 * It exposes only the details needed by the UI without returning the full entity graph.
 */
public class InvestorSummary {
    private Long investorId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private String fullName;

    public InvestorSummary() {
    }

    public InvestorSummary(Long investorId, String firstName, String lastName, String email, LocalDate dateOfBirth, String fullName) {
        this.investorId = investorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.fullName = fullName;
    }

    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
