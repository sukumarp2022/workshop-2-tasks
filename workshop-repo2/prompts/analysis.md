# Analysis Prompt

Use this prompt in Copilot Chat after adding the legacy C++ source files as context.

---

```
Analyze this legacy C++ Employee Management System and identify:

1. All architectural smells (god classes, tight coupling, missing abstractions)
2. C++ patterns that must migrate to Python (malloc/free, char arrays, printf,
   file I/O, #define constants, error codes, global state)
3. Business logic that must be preserved exactly (payroll calculations,
   tax computations, report formats)
4. Technical debt hotspots ranked by migration difficulty
5. Data schema as defined by the .dat file formats

For each finding, provide:
- File and function location
- Why it matters for migration
- Priority: High / Medium / Low

Produce a technical debt heatmap table:
| File | Debt Level (🔴/🟡/🟢) | Key Issues | Migration Effort |
```
