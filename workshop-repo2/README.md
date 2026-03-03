# Employee Management System — Legacy C++ Application

## Overview

This is a **legacy C++ Employee Management System** — a console-based application used as the starting point for a **C++ → Python migration workshop**.

The application manages employees, departments, and payroll using flat-file storage (pipe-delimited `.dat` files).

## Building (Optional)

The application uses CMake:

```bash
cd legacy-app
mkdir build && cd build
cmake ..
cmake --build .
```

> **Note:** You do NOT need to build or run the C++ code. The workshop focuses on **reading** the legacy code and **migrating** it to Python using GitHub Copilot.

## Project Structure

```
workshop-repo2/
├── legacy-app/            ← Legacy C++ source code (READ THIS)
├── modernization/         ← Your analysis & planning documents (WRITE HERE)
├── agents/                ← Custom Copilot agent definitions (CREATE HERE)
├── src-modernized/        ← Your migrated Python code (BUILD HERE)
├── prompts/               ← Reusable prompt templates
└── Workshop - Migrate C++ to Python.md   ← Full workshop guide
```

## Workshop Flow

1. **Analyze** — Understand the legacy C++ code using Copilot
2. **Plan** — Define migration goals, constraints, and success criteria
3. **Create Agent** — Build a custom C++ → Python migration agent
4. **Migrate** — Use the agent to systematically convert to Python
5. **Validate** — Verify the Python code matches C++ behavior

## Legacy Application Features

| Feature | Files |
|---------|-------|
| Employee CRUD | `Employee.h/.cpp`, `FileStorage.cpp` |
| Department Management | `Department.h/.cpp`, `FileStorage.cpp` |
| Payroll Processing | `PayrollRecord.h/.cpp`, `FileStorage.cpp` |
| Report Generation | `ReportGenerator.h/.cpp` |
| Console Interface | `main.cpp` |

## Target Stack (Python)

- Python 3.11+
- FastAPI (REST API)
- SQLAlchemy (Database ORM)
- Pydantic (Data validation)
- SQLite (Database)
- pytest (Testing)

## Sample Data

The `legacy-app/data/` folder contains sample records:
- `employees.dat` — 10 employees across 4 departments
- `departments.dat` — 4 departments (Engineering, Marketing, HR, Finance)
- `payroll.dat` — 7 payroll records for 2 months
