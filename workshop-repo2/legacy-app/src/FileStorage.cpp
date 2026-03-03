// FileStorage.cpp - File-based storage implementation
// WARNING: This code has many legacy issues - intentional for workshop purposes
#include "../include/FileStorage.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>

// Global buffer - shared mutable state (anti-pattern)
static char line_buffer[2048];

void ensure_data_directory() {
#ifdef _WIN32
    _mkdir("data");
#else
    mkdir("data", 0755);
#endif
}

int file_exists(const char* filename) {
    FILE* f = fopen(filename, "r");
    if (f) {
        fclose(f);
        return 1;
    }
    return 0;
}

int get_next_id(const char* filename) {
    FILE* f = fopen(filename, "r");
    if (f == NULL) return 1;

    int max_id = 0;
    int id;
    while (fgets(line_buffer, sizeof(line_buffer), f) != NULL) {
        // Parse first field as ID
        if (sscanf(line_buffer, "%d", &id) == 1) {
            if (id > max_id) max_id = id;
        }
    }
    fclose(f);
    return max_id + 1;
}

// ==================== EMPLOYEE FILE OPERATIONS ====================

int save_employee_to_file(const Employee* emp) {
    if (emp == NULL) return -1;

    ensure_data_directory();

    FILE* f = fopen(EMPLOYEE_FILE, "a");
    if (f == NULL) {
        printf("ERROR: Cannot open employee file for writing\n");
        return -1;
    }

    // CSV format: id|name|email|department|salary|is_active|hire_date
    fprintf(f, "%d|%s|%s|%s|%.2f|%d|%s\n",
            emp->id, emp->name, emp->email, emp->department,
            emp->salary, emp->is_active, emp->hire_date);

    fclose(f);
    return 0;
}

int load_all_employees(Employee** employees, int* count) {
    if (employees == NULL || count == NULL) return -1;

    *count = 0;

    FILE* f = fopen(EMPLOYEE_FILE, "r");
    if (f == NULL) {
        // File doesn't exist yet - not an error
        return 0;
    }

    // First pass: count lines
    int line_count = 0;
    while (fgets(line_buffer, sizeof(line_buffer), f) != NULL) {
        if (strlen(line_buffer) > 1) line_count++;
    }

    if (line_count == 0) {
        fclose(f);
        return 0;
    }

    // Allocate array - caller must free each employee AND the array
    *employees = (Employee*)malloc(sizeof(Employee) * line_count);
    if (*employees == NULL) {
        printf("ERROR: Failed to allocate employee array\n");
        fclose(f);
        return -1;
    }

    // Second pass: parse data
    rewind(f);
    int idx = 0;
    while (fgets(line_buffer, sizeof(line_buffer), f) != NULL && idx < line_count) {
        if (strlen(line_buffer) <= 1) continue;

        // Remove trailing newline
        line_buffer[strcspn(line_buffer, "\n")] = '\0';

        Employee* emp = &(*employees)[idx];

        // Dangerous: no bounds checking on sscanf
        char name[MAX_NAME_LEN], email[MAX_EMAIL_LEN], dept[MAX_DEPT_LEN], hire[11];
        int parsed = sscanf(line_buffer, "%d|%[^|]|%[^|]|%[^|]|%lf|%d|%s",
                           &emp->id, name, email, dept,
                           &emp->salary, &emp->is_active, hire);

        if (parsed == 7) {
            strncpy(emp->name, name, MAX_NAME_LEN - 1);
            strncpy(emp->email, email, MAX_EMAIL_LEN - 1);
            strncpy(emp->department, dept, MAX_DEPT_LEN - 1);
            strncpy(emp->hire_date, hire, 10);
            emp->hire_date[10] = '\0';
            idx++;
        } else {
            printf("WARNING: Skipping malformed employee record: %s\n", line_buffer);
        }
    }

    *count = idx;
    fclose(f);
    return 0;
}

Employee* find_employee_by_id(int employee_id) {
    Employee* employees = NULL;
    int count = 0;

    if (load_all_employees(&employees, &count) != 0) return NULL;

    Employee* found = NULL;
    for (int i = 0; i < count; i++) {
        if (employees[i].id == employee_id) {
            // Clone the found employee before freeing the array
            found = clone_employee(&employees[i]);
            break;
        }
    }

    free(employees);  // Free the array but not individual employees (they're in-place)
    return found;      // Caller must free this
}

int find_employees_by_department(const char* department, Employee** results, int* count) {
    Employee* all_employees = NULL;
    int total = 0;

    if (load_all_employees(&all_employees, &total) != 0) return -1;

    // Worst case: all employees in one department
    *results = (Employee*)malloc(sizeof(Employee) * total);
    if (*results == NULL) {
        free(all_employees);
        return -1;
    }

    *count = 0;
    for (int i = 0; i < total; i++) {
        // Case-insensitive comparison would be better, but this is legacy code
        if (strcmp(all_employees[i].department, department) == 0) {
            memcpy(&(*results)[*count], &all_employees[i], sizeof(Employee));
            (*count)++;
        }
    }

    free(all_employees);
    return 0;
}

int update_employee_in_file(const Employee* emp) {
    if (emp == NULL) return -1;

    Employee* employees = NULL;
    int count = 0;

    if (load_all_employees(&employees, &count) != 0) return -1;

    // Find and update
    int found = 0;
    for (int i = 0; i < count; i++) {
        if (employees[i].id == emp->id) {
            memcpy(&employees[i], emp, sizeof(Employee));
            found = 1;
            break;
        }
    }

    if (!found) {
        free(employees);
        return -1;
    }

    // Rewrite entire file - very inefficient for large datasets
    FILE* f = fopen(EMPLOYEE_FILE, "w");
    if (f == NULL) {
        free(employees);
        return -1;
    }

    for (int i = 0; i < count; i++) {
        fprintf(f, "%d|%s|%s|%s|%.2f|%d|%s\n",
                employees[i].id, employees[i].name, employees[i].email,
                employees[i].department, employees[i].salary,
                employees[i].is_active, employees[i].hire_date);
    }

    fclose(f);
    free(employees);
    return 0;
}

int delete_employee_from_file(int employee_id) {
    Employee* employees = NULL;
    int count = 0;

    if (load_all_employees(&employees, &count) != 0) return -1;

    // Rewrite file without the deleted employee
    FILE* f = fopen(EMPLOYEE_FILE, "w");
    if (f == NULL) {
        free(employees);
        return -1;
    }

    int found = 0;
    for (int i = 0; i < count; i++) {
        if (employees[i].id == employee_id) {
            found = 1;
            continue; // Skip this employee
        }
        fprintf(f, "%d|%s|%s|%s|%.2f|%d|%s\n",
                employees[i].id, employees[i].name, employees[i].email,
                employees[i].department, employees[i].salary,
                employees[i].is_active, employees[i].hire_date);
    }

    fclose(f);
    free(employees);
    return found ? 0 : -1;
}

// ==================== DEPARTMENT FILE OPERATIONS ====================

int save_department_to_file(const Department* dept) {
    if (dept == NULL) return -1;

    ensure_data_directory();

    FILE* f = fopen(DEPARTMENT_FILE, "a");
    if (f == NULL) {
        printf("ERROR: Cannot open department file for writing\n");
        return -1;
    }

    // Save basic dept info, then employee IDs as comma-separated
    fprintf(f, "%d|%s|%s|%d|%.2f|%d",
            dept->id, dept->name, dept->description,
            dept->manager_id, dept->budget, dept->employee_count);

    for (int i = 0; i < dept->employee_count; i++) {
        fprintf(f, "|%d", dept->employee_ids[i]);
    }
    fprintf(f, "\n");

    fclose(f);
    return 0;
}

int load_all_departments(Department** departments, int* count) {
    if (departments == NULL || count == NULL) return -1;
    *count = 0;

    FILE* f = fopen(DEPARTMENT_FILE, "r");
    if (f == NULL) return 0;

    // Count lines
    int line_count = 0;
    while (fgets(line_buffer, sizeof(line_buffer), f) != NULL) {
        if (strlen(line_buffer) > 1) line_count++;
    }

    if (line_count == 0) {
        fclose(f);
        return 0;
    }

    *departments = (Department*)malloc(sizeof(Department) * line_count);
    if (*departments == NULL) {
        fclose(f);
        return -1;
    }

    rewind(f);
    int idx = 0;
    while (fgets(line_buffer, sizeof(line_buffer), f) != NULL && idx < line_count) {
        if (strlen(line_buffer) <= 1) continue;
        line_buffer[strcspn(line_buffer, "\n")] = '\0';

        Department* dept = &(*departments)[idx];
        char name[MAX_DEPT_NAME], desc[MAX_DEPT_DESC];

        // Parse basic fields
        char* token = strtok(line_buffer, "|");
        if (!token) continue;
        dept->id = atoi(token);

        token = strtok(NULL, "|");
        if (token) strncpy(dept->name, token, MAX_DEPT_NAME - 1);

        token = strtok(NULL, "|");
        if (token) strncpy(dept->description, token, MAX_DEPT_DESC - 1);

        token = strtok(NULL, "|");
        if (token) dept->manager_id = atoi(token);

        token = strtok(NULL, "|");
        if (token) dept->budget = atof(token);

        token = strtok(NULL, "|");
        if (token) dept->employee_count = atoi(token);

        // Parse employee IDs
        for (int i = 0; i < dept->employee_count && i < MAX_EMPLOYEES_PER_DEPT; i++) {
            token = strtok(NULL, "|");
            if (token) dept->employee_ids[i] = atoi(token);
        }

        idx++;
    }

    *count = idx;
    fclose(f);
    return 0;
}

Department* find_department_by_id(int department_id) {
    Department* departments = NULL;
    int count = 0;

    if (load_all_departments(&departments, &count) != 0) return NULL;

    Department* found = NULL;
    for (int i = 0; i < count; i++) {
        if (departments[i].id == department_id) {
            found = (Department*)malloc(sizeof(Department));
            if (found) memcpy(found, &departments[i], sizeof(Department));
            break;
        }
    }

    free(departments);
    return found;
}

// ==================== PAYROLL FILE OPERATIONS ====================

int save_payroll_record_to_file(const PayrollRecord* record) {
    if (record == NULL) return -1;

    ensure_data_directory();

    FILE* f = fopen(PAYROLL_FILE, "a");
    if (f == NULL) {
        printf("ERROR: Cannot open payroll file for writing\n");
        return -1;
    }

    fprintf(f, "%d|%d|%s|%.2f|%.1f|%.2f|%.2f|%.2f|%.2f|%d|%s\n",
            record->id, record->employee_id, record->pay_period,
            record->base_salary, record->overtime_hours, record->overtime_rate,
            record->deductions, record->bonus, record->net_pay,
            record->is_processed, record->notes);

    fclose(f);
    return 0;
}

int load_payroll_by_employee(int employee_id, PayrollRecord** records, int* count) {
    if (records == NULL || count == NULL) return -1;
    *count = 0;

    FILE* f = fopen(PAYROLL_FILE, "r");
    if (f == NULL) return 0;

    // Allocate max possible
    *records = (PayrollRecord*)malloc(sizeof(PayrollRecord) * MAX_RECORDS);
    if (*records == NULL) {
        fclose(f);
        return -1;
    }

    while (fgets(line_buffer, sizeof(line_buffer), f) != NULL) {
        if (strlen(line_buffer) <= 1) continue;
        line_buffer[strcspn(line_buffer, "\n")] = '\0';

        PayrollRecord temp;
        char notes[MAX_PAYROLL_NOTES];
        notes[0] = '\0';

        int parsed = sscanf(line_buffer, "%d|%d|%[^|]|%lf|%lf|%lf|%lf|%lf|%lf|%d|%[^\n]",
                           &temp.id, &temp.employee_id, temp.pay_period,
                           &temp.base_salary, &temp.overtime_hours, &temp.overtime_rate,
                           &temp.deductions, &temp.bonus, &temp.net_pay,
                           &temp.is_processed, notes);

        if (parsed >= 10 && temp.employee_id == employee_id) {
            memcpy(&(*records)[*count], &temp, sizeof(PayrollRecord));
            strncpy((*records)[*count].notes, notes, MAX_PAYROLL_NOTES - 1);
            (*count)++;

            if (*count >= MAX_RECORDS) break;
        }
    }

    fclose(f);
    return 0;
}

int process_payroll_batch(const char* pay_period) {
    // Load all payroll records, process unprocessed ones for the period
    FILE* f = fopen(PAYROLL_FILE, "r");
    if (f == NULL) return -1;

    PayrollRecord* records = (PayrollRecord*)malloc(sizeof(PayrollRecord) * MAX_RECORDS);
    if (records == NULL) {
        fclose(f);
        return -1;
    }

    int count = 0;
    int processed = 0;

    while (fgets(line_buffer, sizeof(line_buffer), f) != NULL && count < MAX_RECORDS) {
        if (strlen(line_buffer) <= 1) continue;
        line_buffer[strcspn(line_buffer, "\n")] = '\0';

        PayrollRecord* rec = &records[count];
        char notes[MAX_PAYROLL_NOTES];
        notes[0] = '\0';

        sscanf(line_buffer, "%d|%d|%[^|]|%lf|%lf|%lf|%lf|%lf|%lf|%d|%[^\n]",
               &rec->id, &rec->employee_id, rec->pay_period,
               &rec->base_salary, &rec->overtime_hours, &rec->overtime_rate,
               &rec->deductions, &rec->bonus, &rec->net_pay,
               &rec->is_processed, notes);
        strncpy(rec->notes, notes, MAX_PAYROLL_NOTES - 1);

        if (!rec->is_processed && strcmp(rec->pay_period, pay_period) == 0) {
            calculate_net_pay(rec);
            rec->is_processed = 1;
            processed++;
        }
        count++;
    }
    fclose(f);

    // Rewrite entire file
    f = fopen(PAYROLL_FILE, "w");
    if (f == NULL) {
        free(records);
        return -1;
    }

    for (int i = 0; i < count; i++) {
        fprintf(f, "%d|%d|%s|%.2f|%.1f|%.2f|%.2f|%.2f|%.2f|%d|%s\n",
                records[i].id, records[i].employee_id, records[i].pay_period,
                records[i].base_salary, records[i].overtime_hours, records[i].overtime_rate,
                records[i].deductions, records[i].bonus, records[i].net_pay,
                records[i].is_processed, records[i].notes);
    }

    fclose(f);
    free(records);

    printf("Processed %d payroll records for period %s\n", processed, pay_period);
    return processed;
}
