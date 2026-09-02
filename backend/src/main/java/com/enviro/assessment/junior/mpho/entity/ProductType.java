package com.enviro.assessment.junior.mpho.entity;

/**
 * Product categories used to apply product-specific investment rules.
 * Retirement products follow stricter age-based withdrawal criteria than investment products.
 */
public enum ProductType {
    RETIREMENT,
    INVESTMENT
}
