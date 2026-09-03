# Business Rules

## BR-001: Retirement Withdrawal Age

Retirement withdrawals are permitted only when the investor is older than 65 on the request date.

```text
If product type is RETIREMENT and investor age is 65 or younger, reject the request.
```

| Investor age | Result |
| --- | --- |
| 64 | Rejected |
| 65 | Rejected |
| 66 | Accepted, subject to the remaining rules |

The rule is strictly `age > 65`, not `age >= 65`. It applies only to products classified as `RETIREMENT`; products classified as `INVESTMENT` are not constrained by this age rule.

## BR-002: Available Balance

A withdrawal amount cannot exceed the current available balance of the selected investment product.

```text
If requested amount > available balance, reject the request.
```

Example: a request for R120,000 against a R100,000 balance is rejected.

## BR-003: Maximum Withdrawal Limit

A request cannot exceed 90% of the product's available balance.

$$
maximum\ withdrawal = available\ balance \times 0.90
$$

| Balance | Requested amount | Result |
| --- | --- | --- |
| R100,000 | R89,999 | Accepted |
| R100,000 | R90,000 | Accepted |
| R100,000 | R90,001 | Rejected |

BR-003 is stricter than BR-002 in ordinary cases but both are retained as explicit validation rules.

## BR-004: Positive Amount

The requested amount is required and must be greater than zero. Zero and negative values are rejected. A value such as R0.01 is valid when it also satisfies the other rules.

## BR-005: Remaining Balance

For an approved request, the remaining balance is calculated as:

$$
remaining\ balance = current\ balance - withdrawal\ amount
$$

The product balance is updated to the remaining balance, and that value is stored on the withdrawal notice.

## BR-006: Transactional Processing

Creating the withdrawal notice and updating the product balance form one transaction. A failure during either operation must leave neither change committed. Rejected requests do not change product balances.

## Rule Evaluation Order

1. Validate the request fields and positive amount.
2. Find the investor and investment product.
3. Confirm that the product belongs to the investor's portfolio.
4. Apply the retirement age rule when applicable.
5. Check the available balance and 90% maximum.
6. Create the approved notice and update the balance atomically.