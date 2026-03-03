# Target State

> Document your target architecture from Lab 2 here.

## Target Technology Stack

| Concern | Technology | Version |
|---------|-----------|---------|
| Language | Python | 3.11+ |
| Web Framework | FastAPI | Latest |
| Database | SQLite | Built-in |
| ORM | SQLAlchemy | 2.0+ |
| Data Validation | Pydantic | 2.0+ |
| Configuration | python-dotenv | Latest |
| Testing | pytest | Latest |
| API Documentation | Swagger/OpenAPI | Auto-generated |

## Target Architecture

```
FastAPI Application
├── routers/           ← REST API endpoints (replaces console menu)
│   ├── employees.py
│   ├── departments.py
│   ├── payroll.py
│   └── reports.py
├── services/          ← Business logic (preserves calculations)
│   ├── employee_service.py
│   ├── department_service.py
│   ├── payroll_service.py
│   └── report_service.py
├── repositories/      ← Data access (replaces FileStorage)
│   ├── employee_repo.py
│   ├── department_repo.py
│   └── payroll_repo.py
├── models/            ← SQLAlchemy models (replaces C structs)
├── schemas/           ← Pydantic models (request/response)
└── database.py        ← SQLAlchemy engine setup
```

## API Endpoints (Target)

| Method | Endpoint | Replaces Menu Option |
|--------|----------|---------------------|
| POST | `/api/employees` | 1. Add Employee |
| GET | `/api/employees` | 2. View All Employees |
| GET | `/api/employees/{id}` | 3. Search Employee |
| PUT | `/api/employees/{id}` | 4. Update Employee |
| DELETE | `/api/employees/{id}` | 5. Delete Employee |
| POST | `/api/departments` | 6. Add Department |
| GET | `/api/departments` | 7. View Departments |
| POST | `/api/payroll` | 8. Create Payroll |
| POST | `/api/payroll/process` | 9. Process Payroll |
| GET | `/api/reports/employees` | 10. Employee Report |
| GET | `/api/reports/departments` | 11. Department Summary |
| GET | `/api/reports/payroll/{period}` | 12. Payroll Report |
| GET | `/api/reports/salary-distribution` | 13. Salary Distribution |

## Key Design Decisions

_TODO: Document your architecture decisions_

1.
2.
3.
