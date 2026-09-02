package com.enviro.assessment.junior.mpho.entity;

/**
 * Status of a withdrawal notice after business validation.
 * The current domain treats all valid notices as approved, while the enum leaves room for future rejection states.
 */
public enum WithdrawalStatus {
    APPROVED,
    REJECTED
}
