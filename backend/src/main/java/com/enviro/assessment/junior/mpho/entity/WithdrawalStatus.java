package com.enviro.assessment.junior.mpho.entity;

/**
 * Status of a withdrawal notice after business validation.
 * Requests within the auto-approval threshold (BR-006) are APPROVED immediately; larger
 * requests are held as PENDING for manual review. REJECTED is reserved for that review outcome.
 */
public enum WithdrawalStatus {
    APPROVED,
    PENDING,
    REJECTED
}
