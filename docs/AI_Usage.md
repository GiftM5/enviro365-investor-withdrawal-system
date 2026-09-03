# AI Usage

> How AI-assisted guidance supported the development of the Enviro365 Investor Withdrawal System

## Overview

AI was used as a development companion for planning, explaining concepts, reviewing implementation choices, troubleshooting issues, and improving documentation. It was not treated as an automatic source of code to accept without review. Each suggestion was considered against the assessment requirements, the existing codebase, with my understanding and the behaviour of the running application.

The final implementation, decisions, and verification remained my responsibility. AI assistance was useful for shortening research time and for challenging assumptions while I built and tested the system.

## Planning the Work

The assessment requirements were translated into a practical delivery order before development began. AI helped break the work into focused steps: define the domain entities, set up repositories, implement validation in the service layer, expose REST endpoints, add response DTOs and exception handling, then build the React user interface.

This sequencing helped keep the project scope manageable. It also made it easier to validate each layer before relying on it from the next one.

## Understanding the Backend Design

AI was consulted to clarify several Spring Boot and Java concepts used in the application:

- The responsibility split between controllers, services, repositories, entities, and DTOs.
- Why business rules belong in the service layer rather than only in the frontend.
- How Spring Data JPA repositories simplify persistence for investors, portfolios, products, and withdrawal notices.
- Why `BigDecimal` is appropriate for balances and withdrawal amounts.
- The difference between request validation and domain validation, including ownership, balance, maximum withdrawal, and retirement-age rules.
- How a global exception handler can return consistent error responses from the API.

These discussions informed the layered structure used by the backend rather than replacing design judgment.

## Reviewing Business Rules

AI helped review the mapping of the assessment rules to service-level checks and targeted tests. The project validates that a withdrawal amount is positive, does not exceed the available balance, stays within the 90% limit, belongs to the selected investor, and meets the retirement-product age requirement.

I used these reviews to check boundary conditions, particularly the distinction between investors aged 64, 65, and older than 65 for retirement withdrawals. The resulting rules were verified in the backend test suite.

## Frontend and API Integration

For the React portal, AI was used to discuss how portfolio data should be represented in the API response and displayed in the investor dashboard. This included checking the investor-selection flow, product information, withdrawal history, error handling, and CSV export interaction.

During debugging, AI helped identify that an investor's age was calculated for business validation but was not explicitly included in the portfolio response. The API contract was extended to return the date of birth and calculated age, and the dashboard now displays the investor's age clearly. A focused regression test was added to protect that behaviour.

## Testing and Troubleshooting

AI supported troubleshooting by helping turn observed problems into narrow, verifiable hypotheses. For example, when age was absent from an investor profile, the first check was a focused test against `PortfolioResponse`. The compilation failure showed that the response model did not provide an `age` field, which identified the missing contract rather than a calculation problem.

AI also helped decide which tests were worth adding around the withdrawal service. The test suite covers successful withdrawals, rejected amounts, 90% limit boundaries, retirement eligibility, product ownership, missing resources, unchanged balances after rejection, and portfolio age exposure.

## Documentation Support

AI assisted with organising and refining documentation, including the README, API specification, business rules, database design, architecture notes, decisions, and requirements traceability. The documentation was reviewed against the repository so that commands, endpoints, technologies, and behaviours describe what is actually implemented.

AI was also used to improve the clarity of the project structure and setup instructions for someone running the backend and frontend locally.

## Principles Applied

- **Understand before using.** Suggestions were only applied after their purpose and effect were understood.
- **Verify behaviour.** Changes were checked with tests, builds, application output, or direct inspection.
- **Keep the implementation grounded.** The assessment brief and the repository's existing patterns guided decisions.
- **Use AI for collaboration, not substitution.** AI provided explanations, alternatives, reviews, and debugging support; I remained accountable for the resulting work.

*MPHO GIFT MOFOKENG - eTalente Junior Developer Assessment 2026*