import csv
import random
from datetime import date, timedelta

ROW_COUNT = 100_000
INVALID_RATE = 0.01

currencies = ["USD", "CAD", "EUR", "GBP"]

start_date = date(2026, 1, 1)

with open("transactions-100k.csv", "w", newline="") as file:
    writer = csv.writer(file)

    writer.writerow([
        "transactionId",
        "customerId",
        "amount",
        "currency",
        "transactionDate"
    ])

    for i in range(1, ROW_COUNT + 1):
        transaction_id = f"TXN-{i:09d}"
        customer_id = f"CUST-{random.randint(1, 50000):06d}"
        amount = round(random.uniform(1, 5000), 2)
        currency = random.choice(currencies)
        transaction_date = start_date + timedelta(
            days=random.randint(0, 219)
        )

        # Intentionally make ~1% of rows invalid
        if random.random() < INVALID_RATE:
            amount = "INVALID"

        writer.writerow([
            transaction_id,
            customer_id,
            amount,
            currency,
            transaction_date
        ])

print(f"Generated {ROW_COUNT:,} rows")