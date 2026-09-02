package com.enviro.assessment.junior.mpho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstraps the investor withdrawal API for the Mpho assessment package.
 * This application exposes the investor portfolio and withdrawal workflows used by the portal.
 */
@SpringBootApplication
public class WithdrawalApplication {

    public static void main(String[] args) {
        SpringApplication.run(WithdrawalApplication.class, args);
    }
}