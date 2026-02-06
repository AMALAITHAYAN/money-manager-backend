# Money Manager Backend (Spring Boot + MongoDB + JWT)

Backend for your **Money Manager (Office + Personal)** project.

## Tech
- Java 17
- Spring Boot (Web, Validation, Security)
- MongoDB
- JWT Auth (Bearer token)
- Swagger UI (OpenAPI)
- No Lombok (manual getters/setters)

## Run
1. Start MongoDB locally (or use MongoDB Atlas).
2. Set environment variables (optional):
   - `MONGODB_URI=mongodb://localhost:27017/money_manager`
   - `JWT_SECRET=your_long_random_secret`
   - `CORS_ORIGINS=http://localhost:5173`
3. Start backend:
```bash
mvn spring-boot:run
```

Swagger:
- `http://localhost:8080/swagger-ui.html`

## API Overview

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Accounts
- `POST /api/accounts`
- `GET /api/accounts`

### Transactions
- `POST /api/transactions` (INCOME/EXPENSE)
- `GET /api/transactions` (filters + pagination)
- `GET /api/transactions/{id}`
- `PUT /api/transactions/{id}` (edit allowed only within **12 hours**)
- `DELETE /api/transactions/{id}` (same 12 hours window)

### Transfers
- `POST /api/transfers`

### Analytics
- `GET /api/analytics/dashboard?period=week|month|year&division=OFFICE|PERSONAL`

### Reports
- `GET /api/reports/categories?start=...&end=...&division=...`
- `GET /api/reports/export?start=...&end=...&division=...` (CSV download)

### Budgets
- `POST /api/budgets` (upsert budget per month/category/division)
- `GET /api/budgets?month=YYYY-MM&division=...`
- `GET /api/budgets/status?month=YYYY-MM&division=...`

## Notes
- Transfers are stored as `TransactionType.TRANSFER` and are excluded from Income/Expense analytics.
- Editing restriction is controlled by: `EDIT_WINDOW_HOURS` (default 12).
