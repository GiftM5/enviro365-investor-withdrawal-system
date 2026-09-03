# Database Design

## Entity Model

```text
Investor 1 --- * Portfolio 1 --- * InvestmentProduct 1 --- * WithdrawalNotice
```

The relationship model supports an investor with one or more portfolios and portfolios with multiple products. Withdrawal notices store the investor and product IDs as scalar fields for audit purposes; they do not use JPA object relationships to the investor or product entities.

## Investor

| Field | Purpose |
| --- | --- |
| `id` | Unique identifier |
| `firstName` | Investor first name |
| `lastName` | Investor last name |
| `dateOfBirth` | Calculates retirement eligibility |
| `email` | Contact detail |

## Portfolio

| Field | Purpose |
| --- | --- |
| `id` | Unique identifier |
| `portfolioNumber` | Business-facing portfolio reference |
| `investorId` | Owning investor |

Total balance is derived from the current balances of the portfolio's products rather than stored as a second mutable source of truth.

## Investment Product

| Field | Purpose |
| --- | --- |
| `id` | Unique identifier |
| `portfolioId` | Parent portfolio |
| `productName` | Display name, such as Retirement Annuity |
| `productType` | `RETIREMENT`, `INVESTMENT`, or future classifications |
| `balance` | Current available monetary balance |

Money values use Java `BigDecimal`, never floating-point types.

## Withdrawal Notice

| Field | Purpose |
| --- | --- |
| `id` | Unique withdrawal identifier |
| `investorId` | Requesting investor |
| `productId` | Product withdrawn from |
| `amount` | Approved requested amount |
| `remainingBalance` | Product balance after approval |
| `status` | Initially `APPROVED` for persisted notices |
| `createdAt` | Creation timestamp |

The implementation stores approved notices. Rejected requests are returned as errors and are not persisted.

## Integrity Rules

- A portfolio must belong to an investor.
- A product must belong to a portfolio.
- A withdrawal notice must reference the investor and product involved in the approved request.
- The service verifies ownership before processing a withdrawal.
- Balance updates and notice persistence are executed within one transaction.