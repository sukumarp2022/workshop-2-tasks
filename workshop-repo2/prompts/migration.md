# Migration Prompt

Use this prompt in Copilot Chat with your migration agent and C++ source files as context.

---

## Migrate Data Models

```
Using the rules in @migration-agent.md, migrate these C++ data structures to Python:

@Employee.h → SQLAlchemy model + Pydantic schema
@Department.h → SQLAlchemy model + Pydantic schema
@PayrollRecord.h → SQLAlchemy model + Pydantic schema

Rules:
- char[] → str
- int (as bool) → bool
- double (for money) → float with rounding
- #define constants → Python constants or config
- Fixed-size arrays → Python lists
- Add type hints and docstrings to everything
```

## Migrate Data Access Layer

```
Using @migration-agent.md, migrate file-based storage to SQLAlchemy repositories:

@FileStorage.cpp → repository classes

Transform:
- fopen/fclose → SQLAlchemy session
- malloc arrays → Python lists
- sscanf parsing → ORM queries
- Error code returns → Python exceptions
- Global buffer → no shared state

Each repository needs: save, find_by_id, find_all, update, delete
```

## Migrate Business Logic

```
Using @migration-agent.md, migrate business logic to Python services:

@ReportGenerator.cpp → report_service.py
@PayrollRecord.cpp (calculate_net_pay) → payroll_service.py

CRITICAL: These calculations must produce IDENTICAL results:
- net_pay = base_salary + (overtime_hours * overtime_rate) + bonus - deductions
- federal_tax = net_pay * 0.22
- state_tax = net_pay * 0.05
- retirement = net_pay * 0.06
- insurance = 250.00

Extract hardcoded constants to config. Replace printf with return values.
```

## Create API Endpoints

```
Using @migration-agent.md, create FastAPI endpoints replacing the console menu:

POST /api/employees → Add Employee
GET /api/employees → View All
GET /api/employees/{id} → Search by ID
PUT /api/employees/{id} → Update
DELETE /api/employees/{id} → Delete
POST /api/departments → Add Department
GET /api/departments → View Departments
POST /api/payroll → Create Payroll Record
POST /api/payroll/process → Process Payroll Batch
GET /api/reports/{type} → Generate Reports

Use Pydantic for validation, proper HTTP codes, and FastAPI Depends() for DI.
```
