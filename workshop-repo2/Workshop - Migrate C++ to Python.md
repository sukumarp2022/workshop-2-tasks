# Workshop: Migrate & Modernize C++ Employee Management System to Python

> _C++ → Python, Custom Agents, GitHub Copilot Chat_

---

## Overview

- **Duration:** 2.5 – 3 hours
- **Format:** Instructor-led, guided hands-on workshop
- **Difficulty:** 🟡 Intermediate to 🔴 Advanced
- **Audience:** Developers, Tech Leads, Architects
- **Focus:** Using GitHub Copilot and custom AI agents to migrate a legacy C++ console application to modern Python

---

## Table of Contents

1. [Workshop Purpose](#1-workshop-purpose)
2. [Design Principles](#2-design-principles)
3. [Scope](#3-scope)
4. [Repository Structure](#4-repository-structure)
5. [The Legacy Application](#5-the-legacy-application)
6. [Target Modern Stack](#6-target-modern-stack)
7. [Agent Strategy](#7-agent-strategy)
8. [Agenda & Labs](#8-agenda--labs)
   - [Introduction: Why Agent-Driven Migration?](#introduction-why-agent-driven-migration-️-10-minutes)
   - [Lab 1: Analyze the Legacy C++ Application](#lab-1-analyze-the-legacy-c-application-️-25-minutes)
   - [Lab 2: Define Migration Goals & Constraints](#lab-2-define-migration-goals--constraints-️-20-minutes)
   - [Lab 3: Create a Custom Migration Agent](#lab-3-create-a-custom-migration-agent-️-30-minutes)
   - [Lab 4: Migrate the Application to Python](#lab-4-migrate-the-application-to-python-️-40-minutes)
   - [Lab 5: Validate & Review](#lab-5-validate--review-️-20-minutes)
9. [C++ → Python Migration Patterns](#9-c--python-migration-patterns)
10. [Success Criteria](#10-success-criteria)
11. [Scoring & Achievement Levels](#11-scoring--achievement-levels)
12. [Appendix: Starter Prompts](#appendix-starter-prompts)

---

## 1. Workshop Purpose

This workshop teaches participants how to use **GitHub Copilot** and **custom AI agents** to systematically migrate a **legacy C++ console application** to a **modern Python application** with a REST API, database persistence, and clean architecture.

Participants will move beyond generic "rewrite it" approaches by creating **purpose-built agents** that understand migration goals, constraints, and target patterns — producing repeatable, explainable transformations.

---

## 2. Design Principles

- **Incremental migration** over big-bang rewrites
- **Custom agents** over generic prompting
- **Preserve business logic** — change the HOW, not the WHAT
- **Explicit constraints** and a measurable target state
- **Enterprise-safe** patterns and decisions

---

## 3. Scope

### ✅ Included

- Copilot Chat for analysis and migration
- Custom migration agents (instruction-based, markdown)
- C++ → Python migration patterns
- File-based storage → SQLite with SQLAlchemy
- Console UI → FastAPI REST endpoints
- Manual memory management → Python automatic memory management
- Structured modernization artifacts

### ❌ Excluded

- Full CI/CD pipeline setup
- Cloud deployment
- Frontend/UI development
- Database migration tooling
- Complete production-grade rewrites

---

## 4. Repository Structure

```
workshop-repo2/
├── legacy-app/                    ← Legacy C++ application
│   ├── CMakeLists.txt
│   ├── include/                   ← Header files
│   │   ├── Employee.h
│   │   ├── Department.h
│   │   ├── PayrollRecord.h
│   │   ├── FileStorage.h
│   │   └── ReportGenerator.h
│   ├── src/                       ← Implementation files
│   │   ├── main.cpp
│   │   ├── Employee.cpp
│   │   ├── Department.cpp
│   │   ├── PayrollRecord.cpp
│   │   ├── FileStorage.cpp
│   │   └── ReportGenerator.cpp
│   └── data/                      ← Sample data files
│       ├── employees.dat
│       ├── departments.dat
│       └── payroll.dat
│
├── modernization/                 ← Analysis & planning artifacts
│   ├── current-state.md
│   ├── target-state.md
│   ├── constraints.md
│   └── migration-goals.md
│
├── agents/                        ← Custom agent definitions
│   ├── migration-agent.md
│   └── review-agent.md
│
├── src-modernized/                ← Migrated Python application
│   └── (participants build this)
│
├── prompts/                       ← Reusable prompt templates
│   ├── analysis.md
│   ├── migration.md
│   └── validation.md
│
└── README.md
```

---

## 5. The Legacy Application

The **Employee Management System** is a C++ console application written in C++11 with heavy C-style patterns, representative of legacy enterprise software.

### What It Does

| Feature | Description |
|---------|-------------|
| Employee CRUD | Add, view, search, update, delete employees |
| Departments | Create departments, assign employees |
| Payroll | Create payroll records, process monthly batches |
| Reports | Employee directory, department summary, payroll, salary distribution |

### Key Legacy Characteristics

| Issue | Details |
|-------|---------|
| **Manual memory management** | `malloc`/`free` everywhere, no smart pointers |
| **C-style I/O** | `printf`/`scanf`/`fgets` instead of C++ streams |
| **Flat-file storage** | Pipe-delimited `.dat` files, no database |
| **Global mutable state** | Shared buffers, global flags |
| **God class** | `ReportGenerator` handles formatting, data, calculation, and output |
| **No error abstraction** | Raw integer error codes, no exceptions |
| **Hardcoded constants** | Tax rates, salary brackets, file paths baked in |
| **No dependency injection** | All functions directly call file I/O |
| **Fixed-size arrays** | `MAX_EMPLOYEES_PER_DEPT = 100`, buffer overflows possible |
| **No tests** | Zero unit tests or integration tests |

---

## 6. Target Modern Stack

| Concern | Target Technology |
|---------|-------------------|
| Language | **Python 3.11+** |
| Web Framework | **FastAPI** |
| Database | **SQLite** via **SQLAlchemy** |
| Data Validation | **Pydantic** models |
| Data Models | **Dataclasses** or Pydantic |
| Configuration | **Environment variables** / `.env` files |
| Testing | **pytest** |
| API Docs | **Swagger/OpenAPI** (auto-generated by FastAPI) |
| Dependency Management | **pip** + `requirements.txt` |

---

## 7. Agent Strategy

### 1. 🔍 Analysis Agent
- Understands the legacy C++ codebase
- Maps C++ constructs to Python equivalents
- Identifies technical debt and migration risks

### 2. 🔄 Migration Agent
- Applies C++ → Python transformation rules consistently
- Enforces target stack constraints (FastAPI, SQLAlchemy, Pydantic)
- Produces explainable, reviewable Python code

### 3. ✅ Review Agent
- Compares legacy C++ logic with migrated Python code
- Validates business logic preservation
- Identifies regressions or missing functionality

---

## 8. Agenda & Labs

```
Analyze → Define Goals → Create Agent → Migrate → Validate
```

---

### Introduction: Why Agent-Driven Migration? ⏱️ _10 minutes_

**Why C++ → Python migrations fail:**
- Business logic lost in translation between paradigms
- Inconsistent pattern choices across the migrated codebase
- Memory management patterns don't map 1:1
- No systematic approach — each file migrated differently

**How agents help:**
- Consistent application of migration rules across all files
- Explicit mapping from C++ patterns to Python equivalents
- Reviewable, explainable output for every transformation
- Incremental, safe migration — one component at a time

**✅ Outcome:** Shared understanding of agent-driven migration workflow.

---

### Lab 1: Analyze the Legacy C++ Application ⏱️ _25 minutes_

**Goal:** Build a comprehensive understanding of the legacy codebase.

---

#### 📖 Step-by-Step Walkthrough

**Step 1: Explore the project structure** _(3 min)_

Open the `legacy-app/` folder and review:
- Header files in `include/` — data models and function signatures
- Implementation in `src/` — the actual logic
- Data files in `data/` — sample pipe-delimited records
- `CMakeLists.txt` — build configuration

**Step 2: Identify architectural smells** _(8 min)_

Open Copilot Chat and add the legacy source files as context, then prompt:

```
Analyze this legacy C++ Employee Management System and identify architectural smells:

1. God classes (classes/files doing too much)
2. Tight coupling between components
3. Missing abstraction layers
4. Hardcoded values and magic constants
5. C-style patterns used instead of modern C++ (or Python equivalents)
6. Manual memory management issues
7. Global mutable state
8. Violation of SOLID principles

For each smell, provide:
- Location (file and function)
- Why it's a problem
- Migration priority (High/Medium/Low)
```

✅ **Expected Output:** A prioritized list of 8+ architectural issues.

**Step 3: Identify C++ patterns that need migration** _(5 min)_

Follow-up prompt:

```
Identify C++ patterns in this codebase that must change during migration to Python:

1. malloc/free → Python automatic memory management
2. char arrays/strncpy → Python strings
3. printf/scanf → Python print/input or API endpoints
4. File I/O with fopen/fclose → SQLAlchemy ORM
5. Structs → Python dataclasses or Pydantic models
6. Error codes (return -1) → Python exceptions
7. #define constants → Python constants or config
8. Fixed-size arrays → Python lists/collections

For each, show the C++ pattern found and what the Python equivalent should be.
```

**Step 4: Create technical debt heatmap** _(5 min)_

```
Create a technical debt heatmap for this C++ codebase:

| File | Debt Level | Key Issues | Migration Effort |
|------|-----------|------------|-----------------|

Debt Levels: 🔴 High | 🟡 Medium | 🟢 Low

Focus on issues that will make migration to Python harder.
```

**Step 5: Document findings** _(4 min)_

- Copy your analysis into `modernization/current-state.md`
- Highlight the top 5 migration blockers
- Note dependencies between components

---

#### 💡 Tips

| Challenge | Hint |
|-----------|------|
| 1.1 (architectural smells) | Look at `ReportGenerator.cpp` — it's a god class |
| 1.2 (C++ patterns) | Search for `malloc`, `free`, `printf`, `fopen` |
| 1.3 (debt heatmap) | `FileStorage.cpp` has the most technical debt |
| 1.4 (complexity score) | Count: global variables, function length, coupling |

---

#### ⚠️ Common Pitfalls

- ❌ **Trying to fix C++ first** — Go directly to Python, don't modernize C++
- ❌ **Ignoring the data format** — The `.dat` files define the data schema
- ❌ **Missing business rules** — Tax calculations in `ReportGenerator.cpp` are real business logic

---

#### 🎯 Challenges

| # | Challenge | Difficulty | Points |
|---|-----------|------------|--------|
| 1.1 | Identify 5+ architectural smells | 🟢 Core | 10 |
| 1.2 | Map 5+ C++ patterns to Python equivalents | 🟢 Core | 10 |
| 1.3 | Create technical debt heatmap for all files | 🟡 Challenge | 15 |
| 1.4 | Calculate migration complexity score with justification | 🔴 Bonus | 20 |

**Artifact:** `modernization/current-state.md`

---

### Lab 2: Define Migration Goals & Constraints ⏱️ _20 minutes_

**Goal:** Make migration intent explicit and measurable.

---

#### 📖 Step-by-Step Walkthrough

**Step 1: Define target state** _(5 min)_

```
Based on @current-state.md, define the target state for migrating this C++ app to Python:

1. Target language and version: Python 3.11+
2. Web framework: FastAPI with auto-generated OpenAPI docs
3. Database: SQLite with SQLAlchemy ORM
4. Data validation: Pydantic models
5. Configuration: Environment variables via python-dotenv
6. Testing: pytest with 80%+ coverage target
7. Architecture: Clean layered architecture (routes → services → repositories)

Be specific about what each layer should look like.
```

**Step 2: Define success criteria** _(5 min)_

```
Define measurable success criteria for this C++ to Python migration:

1. All CRUD operations functional via REST API
2. All business logic preserved (payroll calculations, report generation)
3. Data from .dat files importable into SQLite
4. API response times <100ms for standard operations
5. 80%+ test coverage
6. Zero use of subprocess or ctypes to call C++ code
7. Clean Swagger documentation for all endpoints

Each criterion must be testable.
```

**Step 3: Identify constraints** _(5 min)_

```
Identify constraints for this migration:

1. Business logic must produce identical outputs for identical inputs
2. All existing data must be migratable from .dat files
3. No external paid services or APIs required
4. Must run with `pip install` and `uvicorn` — no complex setup
5. Python standard library + FastAPI + SQLAlchemy + Pydantic only
6. Report output format must be equivalent (not necessarily identical)

For each, note the consequence if violated.
```

**Step 4: Create phased migration plan** _(5 min)_

```
Create a phased migration plan:

Phase 1: Foundation (Data Models & Database)
- Migrate structs → Pydantic/SQLAlchemy models
- Set up SQLite database with schema
- Create data import script for .dat files

Phase 2: Core Logic (Services & Repositories)
- Migrate business logic to Python service classes
- Implement repository pattern for database access
- Preserve all calculations and rules

Phase 3: API Layer (FastAPI Endpoints)
- Create REST endpoints for all CRUD operations
- Add request validation and error handling
- Generate OpenAPI documentation

Phase 4: Reports & Testing
- Migrate report generation logic
- Write comprehensive tests
- Validate against original outputs

For each phase, define success criteria and rollback strategy.
```

---

#### 🎯 Challenges

| # | Challenge | Difficulty | Points |
|---|-----------|------------|--------|
| 2.1 | Define 3+ measurable success criteria | 🟢 Core | 10 |
| 2.2 | Identify 3+ hard constraints | 🟢 Core | 10 |
| 2.3 | Create prioritized migration roadmap | 🟡 Challenge | 15 |
| 2.4 | Define rollback strategy per phase | 🔴 Bonus | 20 |

**Artifacts:** `modernization/migration-goals.md`, `modernization/constraints.md`

---

### Lab 3: Create a Custom Migration Agent ⏱️ _30 minutes_

**Goal:** Build a purpose-driven agent that knows how to migrate C++ to Python.

---

#### 📖 Step-by-Step Walkthrough

**Step 1: Define agent identity** _(5 min)_

Create `agents/migration-agent.md` and start with:

```markdown
# C++ to Python Migration Agent

## Identity
You are a C++ to Python migration expert specializing in converting legacy
C/C++ applications to modern Python with FastAPI, SQLAlchemy, and Pydantic.

## Your Responsibilities
1. Analyze legacy C++ code and produce equivalent Python
2. Apply consistent migration rules across all components
3. Preserve business logic exactly — same inputs must produce same outputs
4. Explain the reasoning behind every transformation
5. Flag any ambiguous logic for human review

## You Must NOT
1. Change business logic or calculation formulas
2. Skip error handling — convert C error codes to Python exceptions
3. Introduce dependencies outside the approved stack
4. Leave any C-style patterns in the Python code
5. Use global mutable state in the Python version
```

**Step 2: Define transformation rules** _(10 min)_

Add specific C++ → Python mapping rules:

```markdown
## Transformation Rules

### Data Types
| C++ Pattern | Python Equivalent |
|-------------|-------------------|
| `struct` with fields | Pydantic `BaseModel` or SQLAlchemy model |
| `char[]` / `char*` | `str` |
| `int` (as bool) | `bool` |
| `double` | `float` with `Decimal` for money |
| `#define MAX_X` | Class constant or config value |
| Fixed-size arrays | `list[T]` |

### Memory Management
- Remove ALL `malloc`/`free`/`memcpy` — Python manages memory
- Remove ALL null pointer checks for memory — not needed in Python
- Keep null/None checks for business logic validation

### I/O Patterns
| C++ Pattern | Python Equivalent |
|-------------|-------------------|
| `printf()` / `fprintf()` | `logging` module or return values |
| `scanf()` / `fgets()` | FastAPI request body (Pydantic model) |
| `fopen`/`fclose` file I/O | SQLAlchemy ORM operations |
| Pipe-delimited `.dat` files | SQLite database tables |

### Error Handling
| C++ Pattern | Python Equivalent |
|-------------|-------------------|
| Return `-1` on error | Raise specific exception |
| Return `NULL` on failure | Raise `ValueError` or `HTTPException` |
| `printf("ERROR: ...")` | `logging.error()` + raise exception |
| Error code integers | Custom exception classes |

### Architecture
| C++ Pattern | Python Equivalent |
|-------------|-------------------|
| Header + implementation files | Single `.py` module per concern |
| Free functions | Class methods on service/repository |
| Global state | Dependency-injected services |
| `main()` menu loop | FastAPI router endpoints |
| `#include` | `import` / `from ... import` |
```

**Step 3: Add validation checklist** _(8 min)_

```markdown
## Self-Validation Checklist

Before returning migrated code, verify:

- [ ] Business logic produces identical results for same inputs
- [ ] All C++ functions have a Python equivalent
- [ ] No `malloc`, `free`, `printf`, `scanf` patterns remain
- [ ] Proper Python exceptions replace C error codes
- [ ] All magic numbers extracted to named constants or config
- [ ] Type hints on all function signatures
- [ ] Docstrings on all public functions
- [ ] SQLAlchemy models match the data schema from .dat files
- [ ] FastAPI endpoints cover all CRUD operations
- [ ] Pydantic models validate all input data
```

**Step 4: Add before/after examples** _(7 min)_

Use Copilot to help generate examples:

```
Provide 3 before/after migration examples for my agent:

Example 1: Struct → Pydantic Model
Example 2: File I/O → SQLAlchemy Repository
Example 3: Error Codes → Python Exceptions

For each, show the C++ code, the Python equivalent, and WHY the change was made.
Use code from the legacy-app/ as the source.
```

---

#### 🎯 Challenges

| # | Challenge | Difficulty | Points |
|---|-----------|------------|--------|
| 3.1 | Define 5+ specific migration rules | 🟢 Core | 10 |
| 3.2 | Include error handling migration instructions | 🟢 Core | 10 |
| 3.3 | Add C++ → Python transformation patterns for all data types | 🟡 Challenge | 15 |
| 3.4 | Create agent self-validation checklist | 🟡 Challenge | 15 |
| 3.5 | Build versioned agent with changelog | 🔴 Bonus | 25 |

**Artifact:** `agents/migration-agent.md`

---

### Lab 4: Migrate the Application to Python ⏱️ _40 minutes_

**Goal:** Use the agent to systematically migrate C++ components to Python.

---

#### 📖 Step-by-Step Walkthrough

**Step 1: Set up the Python project** _(3 min)_

Create the target project structure in `src-modernized/`:

```
src-modernized/
├── app/
│   ├── __init__.py
│   ├── main.py              ← FastAPI application entry point
│   ├── config.py             ← Configuration (replaces #defines)
│   ├── models/
│   │   ├── __init__.py
│   │   ├── employee.py       ← Employee model
│   │   ├── department.py     ← Department model
│   │   └── payroll.py        ← PayrollRecord model
│   ├── schemas/
│   │   ├── __init__.py
│   │   ├── employee.py       ← Pydantic request/response schemas
│   │   ├── department.py
│   │   └── payroll.py
│   ├── repositories/
│   │   ├── __init__.py
│   │   ├── employee_repo.py  ← Replaces FileStorage employee ops
│   │   ├── department_repo.py
│   │   └── payroll_repo.py
│   ├── services/
│   │   ├── __init__.py
│   │   ├── employee_service.py
│   │   ├── department_service.py
│   │   ├── payroll_service.py
│   │   └── report_service.py ← Replaces ReportGenerator
│   ├── routers/
│   │   ├── __init__.py
│   │   ├── employees.py      ← REST endpoints
│   │   ├── departments.py
│   │   ├── payroll.py
│   │   └── reports.py
│   └── database.py           ← SQLAlchemy setup
├── tests/
│   ├── __init__.py
│   ├── test_employee_service.py
│   ├── test_payroll_service.py
│   └── test_api.py
├── data/
│   └── import_legacy_data.py ← Script to import .dat files
├── requirements.txt
├── .env.example
└── README.md
```

**Step 2: Migrate data models** _(10 min)_

Open Copilot Chat with your migration agent and the C++ headers:

```
Using the rules in @migration-agent.md, migrate these C++ data structures to Python:

@Employee.h
@Department.h
@PayrollRecord.h

Create:
1. SQLAlchemy models in app/models/ (database tables)
2. Pydantic schemas in app/schemas/ (API request/response)

Preserve all fields. Replace char arrays with str, int-as-bool with bool.
Replace #define constants with Python constants.
Add type hints and docstrings.
```

✅ **Expected Output:** Clean Python model files with proper type hints.

**Step 3: Migrate data access layer** _(12 min)_

```
Using @migration-agent.md, migrate the data access logic to Python:

@FileStorage.cpp → app/repositories/

Transform:
1. File I/O (fopen/fclose) → SQLAlchemy session queries
2. malloc/free arrays → Python lists
3. sscanf parsing → SQLAlchemy ORM mapping
4. Error code returns → Python exceptions
5. Global buffer → no shared mutable state

Create repository classes with these methods matching the original functions:
- save, find_by_id, find_all, update, delete
- find_by_department (employees)
- find_by_employee (payroll)
- process_batch (payroll)

Show before/after for each function.
```

**Step 4: Migrate business logic** _(10 min)_

```
Using @migration-agent.md, migrate the business logic to Python services:

@ReportGenerator.cpp → app/services/report_service.py
@PayrollRecord.cpp (calculate_net_pay) → app/services/payroll_service.py

Critical: The payroll calculation and tax computation must produce
IDENTICAL results. Preserve these formulas exactly:
- net_pay = base_salary + (overtime_hours * overtime_rate) + bonus - deductions
- federal_tax = net_pay * 0.22
- state_tax = net_pay * 0.05
- retirement = net_pay * 0.06
- insurance = 250.00
- take_home = net_pay - federal_tax - state_tax - retirement - insurance

Extract hardcoded constants to config.py.
Replace printf output with return values (dicts/dataclasses).
```

**Step 5: Create API endpoints** _(5 min)_

```
Using @migration-agent.md, create FastAPI REST endpoints that replace the
console menu in main.cpp:

Menu option 1 (Add Employee) → POST /api/employees
Menu option 2 (View All) → GET /api/employees
Menu option 3 (Search by ID) → GET /api/employees/{id}
Menu option 4 (Update) → PUT /api/employees/{id}
Menu option 5 (Delete) → DELETE /api/employees/{id}
Menu option 6 (Add Dept) → POST /api/departments
Menu option 7 (View Depts) → GET /api/departments
Menu option 8 (Create Payroll) → POST /api/payroll
Menu option 9 (Process Payroll) → POST /api/payroll/process
Menu options 10-13 (Reports) → GET /api/reports/{type}

Use proper HTTP status codes, Pydantic validation, and error responses.
```

---

#### 💡 Tips

| Challenge | Hint |
|-----------|------|
| 4.1 (migrate models) | Match `.dat` file columns to model fields exactly |
| 4.2 (deprecated patterns) | Search for every `malloc` and `printf` in the C++ code |
| 4.3 (async) | FastAPI supports `async def` endpoints — use them |
| 4.4 (DI) | Use FastAPI's `Depends()` for dependency injection |
| 4.5 (strangler fig) | Keep C++ running, add Python API alongside it |

---

#### ⚠️ Common Pitfalls

- ❌ **Changing calculations** — The payroll math MUST match exactly
- ❌ **Missing data fields** — Every field in the C++ struct must appear in Python
- ❌ **Ignoring edge cases** — The C++ code has a negative-pay bug; document it, don't fix it silently
- ❌ **Over-engineering** — Keep it simple; match the original scope

---

#### 🎯 Challenges

| # | Challenge | Difficulty | Points |
|---|-----------|------------|--------|
| 4.1 | Migrate all 3 data models to SQLAlchemy + Pydantic | 🟢 Core | 15 |
| 4.2 | Migrate FileStorage to repository classes | 🟢 Core | 10 |
| 4.3 | Migrate business logic to Python services with identical results | 🟡 Challenge | 20 |
| 4.4 | Create FastAPI endpoints with dependency injection | 🟡 Challenge | 15 |
| 4.5 | Create data import script for `.dat` files | 🔴 Bonus | 30 |

**Output:** Complete Python application in `src-modernized/`

---

### Lab 5: Validate & Review ⏱️ _20 minutes_

**Goal:** Ensure the migrated Python app matches the legacy C++ behavior.

---

#### 📖 Step-by-Step Walkthrough

**Step 1: Create a review agent** _(5 min)_

Create `agents/review-agent.md`:

```markdown
# Migration Review Agent

## Identity
You are a code review expert specializing in C++ to Python migrations.

## Your Review Checklist
1. Compare C++ function signatures with Python method signatures
2. Verify all business logic calculations produce identical results
3. Check that all data fields are preserved
4. Validate error handling covers all original error paths
5. Ensure no C-style patterns leaked into Python code

## Review Output Format
For each component, produce:
- ✅ PASS or ❌ FAIL
- Side-by-side comparison of key logic
- List of concerns or regressions found
- Recommended tests to add
```

**Step 2: Compare components** _(5 min)_

```
Using @review-agent.md, compare legacy vs migrated code:

Legacy: @Employee.cpp + @Employee.h
Migrated: @employee.py (model) + @employee.py (schema)

Verify:
1. All fields present with correct types
2. Validation logic preserved (validate_employee → Pydantic validators)
3. No data loss in type conversions
```

**Step 3: Validate business logic** _(5 min)_

```
Compare the payroll calculation logic:

Legacy: @PayrollRecord.cpp (calculate_net_pay) + @ReportGenerator.cpp (tax calc)
Migrated: @payroll_service.py

Using the sample data in employees.dat and payroll.dat:
- Calculate expected net_pay for employee ID 1, period 2025-01
- Verify Python produces the same result
- Flag any discrepancies
```

**Step 4: Generate validation tests** _(5 min)_

```
Generate pytest test cases that validate the migration:

1. Test payroll calculation matches C++ output for all sample records
2. Test employee CRUD operations via API
3. Test department operations
4. Test report generation produces equivalent output
5. Test data import from .dat files

Use the sample data files as test fixtures.
```

---

#### 🎯 Challenges

| # | Challenge | Difficulty | Points |
|---|-----------|------------|--------|
| 5.1 | Create before/after comparison for 3+ components | 🟢 Core | 10 |
| 5.2 | Verify payroll calculation with sample data | 🟡 Challenge | 15 |
| 5.3 | Identify 2+ potential regressions with mitigation plans | 🟡 Challenge | 15 |
| 5.4 | Generate automated regression test suite with pytest | 🔴 Bonus | 25 |

**Artifact:** `modernization/validation.md`

---

## 9. C++ → Python Migration Patterns

### Data Types

| C++ | Python |
|-----|--------|
| `int` | `int` |
| `double` | `float` (or `Decimal` for money) |
| `char[N]` / `char*` | `str` |
| `int` (0/1 as bool) | `bool` |
| `struct` | `dataclass`, Pydantic `BaseModel`, or SQLAlchemy `Model` |
| `enum` | `enum.Enum` |
| Fixed-size array `T[N]` | `list[T]` |
| `NULL` | `None` |

### Memory & Pointers

| C++ Pattern | Python Equivalent |
|-------------|-------------------|
| `malloc(sizeof(T))` | Just create the object: `T()` |
| `free(ptr)` | Automatic garbage collection |
| `memcpy(dst, src, n)` | Assignment or `copy.deepcopy()` |
| `ptr == NULL` check | `if obj is None` (only for business logic) |
| `strncpy(dst, src, n)` | `dst = src` (strings are immutable) |

### I/O & Storage

| C++ Pattern | Python Equivalent |
|-------------|-------------------|
| `printf(fmt, ...)` | `logging.info()` or f-strings |
| `scanf("%d", &var)` | FastAPI request body parsing |
| `fopen/fclose` + `fprintf/fgets` | SQLAlchemy session + ORM |
| Pipe-delimited flat files | SQLite relational tables |
| `sscanf` parsing | Pydantic model validation |

### Error Handling

| C++ Pattern | Python Equivalent |
|-------------|-------------------|
| `return -1;` | `raise ValueError("...")` |
| `return NULL;` | `raise HTTPException(404)` |
| `printf("ERROR: ...\n")` | `logging.error("...")` |
| Integer error codes | Custom exception hierarchy |

### Architecture

| C++ Pattern | Python Equivalent |
|-------------|-------------------|
| `.h` + `.cpp` file pairs | Single `.py` module |
| Free functions | Class methods |
| `#define CONSTANT` | Module-level `CONSTANT` or `.env` |
| `#include` | `import` / `from x import y` |
| `main()` switch/menu | FastAPI router + endpoints |
| Global mutable state | Dependency-injected services |

---

## 10. Success Criteria

By the end of this workshop, participants will have:

- [ ] Analyzed and documented a legacy C++ application
- [ ] Mapped all C++ patterns to Python equivalents
- [ ] Defined explicit migration goals and constraints
- [ ] Created a custom C++ → Python migration agent
- [ ] Migrated data models (structs → Pydantic/SQLAlchemy)
- [ ] Migrated data access (file I/O → SQLAlchemy repositories)
- [ ] Migrated business logic with identical calculation results
- [ ] Created REST API endpoints replacing the console menu
- [ ] Validated the migration against original behavior
- [ ] Written tests to verify business logic preservation

---

## 11. Scoring & Achievement Levels

### Point Breakdown

| Category | Points Available |
|----------|------------------|
| 🟢 Core Challenges | 95 points |
| 🟡 Challenge Tasks | 110 points |
| 🔴 Bonus Tasks | 120 points |
| **Total Possible** | **325 points** |

### Achievement Levels

| Level | Points | Badge |
|-------|--------|-------|
| 🥇 **Migration Master** | 275+ | Expert-level cross-language migration skills |
| 🥈 **Transformation Lead** | 195–274 | Strong migration execution |
| 🥉 **Migration Specialist** | 115–194 | Solid foundational understanding |
| **Participant** | <115 | Completed core workshop activities |

### Time Bonuses

| Completion Time | Bonus |
|-----------------|-------|
| Under 2 hours | +30 points |
| Under 2.5 hours | +15 points |

---

## Appendix: Starter Prompts

### Analysis Prompt
```
Analyze this legacy C++ Employee Management System and identify:

1. All C++ patterns that need migration to Python
2. Memory management issues (malloc/free usage)
3. Technical debt hotspots
4. Business logic that must be preserved exactly
5. Data schema defined by .dat file formats

Prioritize findings by migration difficulty and business impact.
```

### Migration Goals Prompt
```
Help me define migration goals for this C++ to Python project:

Current state: Legacy C++ console app with file-based storage
Target: Modern Python REST API with SQLite database

Define:
1. Target Python version and framework stack
2. Architecture style (layered: routes → services → repos)
3. Specific patterns to adopt (Pydantic, SQLAlchemy, FastAPI)
4. Data migration strategy (.dat files → SQLite)
5. What is explicitly out of scope
```

### Migration Agent Prompt
```
You are a C++ to Python migration agent for an Employee Management System.

Your responsibilities:
1. Convert C++ structs to Pydantic/SQLAlchemy models
2. Replace file I/O with SQLAlchemy ORM operations
3. Convert error codes to Python exceptions
4. Replace console menu with FastAPI REST endpoints
5. Eliminate all manual memory management

Target stack: Python 3.11+, FastAPI, SQLAlchemy, Pydantic, pytest

Rules:
- Preserve business logic calculations EXACTLY
- Use type hints on all functions
- Add docstrings to all public methods
- Extract hardcoded constants to configuration
```

### Migration Prompt
```
Migrate this C++ component to Python according to our migration goals:

[C++ code]

Apply these transformations:
1. Convert data structures to Pydantic models
2. Replace file operations with SQLAlchemy queries
3. Convert error codes to exceptions
4. Add proper Python type hints
5. Add docstrings explaining the original C++ function

Do NOT change: business logic, calculation formulas, data relationships
```

### Validation Prompt
```
Compare this migrated Python code against the original C++:

Original C++: [legacy code]
Migrated Python: [new code]

Verify:
1. Business logic produces identical results
2. All data fields are preserved
3. Error cases are handled equivalently
4. No C-style patterns remain in Python
5. All constraints from migration-goals.md are respected

Flag any concerns with severity level (Critical/Warning/Info).
```
