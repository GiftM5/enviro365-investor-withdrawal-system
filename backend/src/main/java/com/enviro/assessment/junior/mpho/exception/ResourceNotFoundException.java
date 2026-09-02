package com.enviro.assessment.junior.mpho.exception;

/**
 * Raised when an investor, portfolio, or product cannot be found by ID.
 * The API translates this into a 404 response for the requesting client.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
