// Employee.cpp - Employee implementation with legacy patterns
#include "../include/Employee.h"

Employee* create_employee(int id, const char* name, const char* email,
                          const char* department, double salary, const char* hire_date) {
    // Manual heap allocation - no smart pointers
    Employee* emp = (Employee*)malloc(sizeof(Employee));
    if (emp == NULL) {
        printf("ERROR: Failed to allocate memory for employee\n");
        return NULL;
    }

    emp->id = id;
    strncpy(emp->name, name, MAX_NAME_LEN - 1);
    emp->name[MAX_NAME_LEN - 1] = '\0';
    strncpy(emp->email, email, MAX_EMAIL_LEN - 1);
    emp->email[MAX_EMAIL_LEN - 1] = '\0';
    strncpy(emp->department, department, MAX_DEPT_LEN - 1);
    emp->department[MAX_DEPT_LEN - 1] = '\0';
    emp->salary = salary;
    emp->is_active = 1;
    strncpy(emp->hire_date, hire_date, 10);
    emp->hire_date[10] = '\0';

    return emp;
}

void destroy_employee(Employee* emp) {
    if (emp != NULL) {
        free(emp);
    }
}

Employee* clone_employee(const Employee* emp) {
    if (emp == NULL) return NULL;

    Employee* clone = (Employee*)malloc(sizeof(Employee));
    if (clone == NULL) {
        printf("ERROR: Failed to allocate memory for employee clone\n");
        return NULL;
    }
    // Shallow copy via memcpy (works here since no pointers in struct)
    memcpy(clone, emp, sizeof(Employee));
    return clone;
}

void print_employee(const Employee* emp) {
    if (emp == NULL) {
        printf("ERROR: NULL employee pointer\n");
        return;
    }
    printf("========================================\n");
    printf("Employee ID:   %d\n", emp->id);
    printf("Name:          %s\n", emp->name);
    printf("Email:         %s\n", emp->email);
    printf("Department:    %s\n", emp->department);
    printf("Salary:        $%.2f\n", emp->salary);
    printf("Status:        %s\n", emp->is_active ? "Active" : "Inactive");
    printf("Hire Date:     %s\n", emp->hire_date);
    printf("========================================\n");
}

int validate_employee(const Employee* emp) {
    if (emp == NULL) return -1;
    if (emp->id <= 0) return -2;
    if (strlen(emp->name) == 0) return -3;
    if (strlen(emp->email) == 0) return -4;

    // Crude email validation - just check for '@'
    int has_at = 0;
    for (int i = 0; i < (int)strlen(emp->email); i++) {
        if (emp->email[i] == '@') {
            has_at = 1;
            break;
        }
    }
    if (!has_at) return -5;

    if (emp->salary < 0) return -6;

    return 0; // success
}
