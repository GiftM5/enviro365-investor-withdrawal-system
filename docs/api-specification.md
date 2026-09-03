# API Specification

## Architecture

The Enviro365 Investor Withdrawal System uses a layered client-server design:

```text
React + Vite frontend
  |
  | HTTP/JSON requests
  v
Spring Boot REST controllers
  |
  v
Service layer: withdrawal rules and transaction handling
  |
  v
Spring Data JPA repositories
  |
  v
H2 in-memory database
```

The frontend lets users select an investor, view portfolio details, submit withdrawal notices, and review or export history. REST controllers expose the `/api` endpoints, services enforce the business rules, repositories persist the domain data, and DTOs define the request and response contracts. A global exception handler returns consistent error responses to the frontend.

## Conventions

All JSON endpoints use the `/api` prefix. Responses use HTTP status codes, DTOs, and a common error shape. Monetary values are represented as decimal values. The CSV export currently returns a download response with `text/plain` content and a `.csv` filename.

## Get Investor Portfolio

`GET /api/investors/{investorId}/portfolio`

Returns investor details, portfolio information, and investment products with current balances and maximum withdrawal amounts.

**Success: `200 OK`**

```json
{
  "investorId": 1,
  "investorName": "John Smith",
  "portfolioNumber": "ENV-10001",
  "products": [
    {
      "productId": 10,
      "productName": "Retirement Annuity",
      "productType": "RETIREMENT",
      "balance": 200000.00,
      "maximumWithdrawal": 180000.00
    }
  ]
}
```

**Error: `404 Not Found`** when the investor does not exist.

## Create Withdrawal Notice

`POST /api/withdrawals`

Creates an approved withdrawal notice after all business rules pass.

**Request**

```json
{
  "investorId": 1,
  "productId": 10,
  "amount": 50000.00
}
```

**Success: `201 Created`**

```json
{
  "withdrawalId": 5001,
  "investorId": 1,
  "productId": 10,
  "amount": 50000.00,
  "previousBalance": 100000.00,
  "remainingBalance": 50000.00,
  "status": "APPROVED",
  "createdAt": "2026-09-01T10:30:00Z"
}
```

**Errors**

| Status | Condition |
| --- | --- |
| `400` | Missing or non-positive amount; ownership failure; age, balance, or 90% rule violation |
| `404` | Investor or product not found |

## Get Withdrawal History

`GET /api/investors/{investorId}/withdrawals`

Returns the investor's approved withdrawal notices, ordered newest first. The response contains the persisted notice fields: `id`, `investorId`, `productId`, `amount`, `previousBalance`, `remainingBalance`, `status`, and `createdAt`.

**Success: `200 OK`** returns a list of withdrawal notices.

## Export Withdrawal History

`GET /api/investors/{investorId}/withdrawals/export`

Returns a CSV statement for the investor. Supported optional filters are `productId`, `from`, `to`, and `status`. All filters combine when supplied. The `from` and `to` dates are inclusive and compare against the notice creation date.

Example:

```text
GET /api/investors/1/withdrawals/export?from=2026-01-01&to=2026-06-30&status=APPROVED
```

The export headers are `id,date,product_id,product_name,amount,status,previous_balance,remaining_balance`. Empty results return a valid CSV with headers and no data rows. Product names are looked up from the current product records; missing products are exported as `Unknown`.

## Error Response

```json
{
  "timestamp": "2026-09-01T10:30:00Z",
  "status": 400,
  "error": "Withdrawal Validation Failed",
  "message": "Withdrawal amount exceeds the maximum allowed amount.",
  "path": "/api/withdrawals"
}
```

The configured handlers do not expose stack traces or database details. Business validation messages are returned in the `message` field so the frontend can display the reason for rejection.