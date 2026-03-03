# Migration Constraints

> Document constraints from Lab 2 here.

## Hard Constraints (Must NOT Violate)

_TODO: Identify 3+ constraints. Example format:_

| # | Constraint | Consequence if Violated |
|---|-----------|------------------------|
| 1 | Business logic must produce identical outputs for identical inputs | Payroll calculations would be incorrect — legal/financial risk |
| 2 | All existing data must be migratable from `.dat` files | Data loss — unacceptable |
| 3 | | |

## Soft Constraints (Preferred)

| # | Constraint | Reason |
|---|-----------|--------|
| 1 | Minimize external dependencies | Easier maintenance |
| 2 | | |

## Technology Constraints

- **Approved dependencies only:** FastAPI, SQLAlchemy, Pydantic, python-dotenv, pytest, uvicorn
- **No paid services or external APIs**
- **Must run with:** `pip install -r requirements.txt` and `uvicorn app.main:app`
- **No Docker required** for basic operation

## Business Constraints

_TODO: List business constraints_

1.
2.
3.
