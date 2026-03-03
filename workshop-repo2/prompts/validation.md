# Validation Prompt

Use this prompt in Copilot Chat with your review agent, legacy C++ code, and migrated Python code as context.

---

## Component Comparison

```
Using @review-agent.md, compare legacy vs migrated code:

Legacy: @Employee.cpp + @Employee.h
Migrated: @employee.py (model) + @employee.py (schema)

Verify:
1. All fields present with correct types
2. Validation logic preserved
3. No data loss in type conversions
4. Proper error handling

Produce a side-by-side comparison table.
```

## Business Logic Verification

```
Compare the payroll calculation logic:

Legacy: @PayrollRecord.cpp (calculate_net_pay) + @ReportGenerator.cpp (tax calc)
Migrated: @payroll_service.py

Test with sample data from payroll.dat:
- Employee 1, period 2025-01: base=$7916.67, OT=10h@$75, deductions=$500, bonus=$1000
  Expected net_pay = 7916.67 + (10 * 75) + 1000 - 500 = $9166.67

Verify Python produces the SAME result. Flag any discrepancy.
```

## Regression Test Generation

```
Generate pytest test cases that validate the complete migration:

1. Payroll calculation tests (use ALL sample records from payroll.dat)
2. Employee CRUD via API (create, read, update, delete)
3. Department operations
4. Report generation equivalence
5. Data import from .dat files
6. Edge cases: empty input, invalid data, negative values

Use pytest fixtures and sample data from legacy-app/data/ as test data.
```
