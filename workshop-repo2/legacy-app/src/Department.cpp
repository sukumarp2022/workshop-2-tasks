// Department.cpp - Department implementation
#include "../include/Department.h"
#include <stdlib.h>

Department* create_department(int id, const char* name, const char* description,
                              int manager_id, double budget) {
    Department* dept = (Department*)malloc(sizeof(Department));
    if (dept == NULL) {
        printf("ERROR: Failed to allocate memory for department\n");
        return NULL;
    }

    dept->id = id;
    strncpy(dept->name, name, MAX_DEPT_NAME - 1);
    dept->name[MAX_DEPT_NAME - 1] = '\0';
    strncpy(dept->description, description, MAX_DEPT_DESC - 1);
    dept->description[MAX_DEPT_DESC - 1] = '\0';
    dept->manager_id = manager_id;
    dept->budget = budget;
    dept->employee_count = 0;

    // Initialize employee_ids array to 0
    memset(dept->employee_ids, 0, sizeof(dept->employee_ids));

    return dept;
}

void destroy_department(Department* dept) {
    if (dept != NULL) {
        free(dept);
    }
}

void print_department(const Department* dept) {
    if (dept == NULL) {
        printf("ERROR: NULL department pointer\n");
        return;
    }
    printf("========================================\n");
    printf("Department ID:    %d\n", dept->id);
    printf("Name:             %s\n", dept->name);
    printf("Description:      %s\n", dept->description);
    printf("Manager ID:       %d\n", dept->manager_id);
    printf("Budget:           $%.2f\n", dept->budget);
    printf("Employee Count:   %d\n", dept->employee_count);
    printf("Employee IDs:     ");
    for (int i = 0; i < dept->employee_count; i++) {
        printf("%d", dept->employee_ids[i]);
        if (i < dept->employee_count - 1) printf(", ");
    }
    printf("\n");
    printf("========================================\n");
}

int department_add_employee(Department* dept, int employee_id) {
    if (dept == NULL) return -1;
    if (dept->employee_count >= MAX_EMPLOYEES_PER_DEPT) {
        printf("ERROR: Department %s is full (max %d employees)\n",
               dept->name, MAX_EMPLOYEES_PER_DEPT);
        return -1;
    }

    // Check for duplicates
    for (int i = 0; i < dept->employee_count; i++) {
        if (dept->employee_ids[i] == employee_id) {
            printf("WARNING: Employee %d already in department %s\n",
                   employee_id, dept->name);
            return -1;
        }
    }

    dept->employee_ids[dept->employee_count] = employee_id;
    dept->employee_count++;
    return 0;
}

int department_remove_employee(Department* dept, int employee_id) {
    if (dept == NULL) return -1;

    for (int i = 0; i < dept->employee_count; i++) {
        if (dept->employee_ids[i] == employee_id) {
            // Shift remaining elements left
            for (int j = i; j < dept->employee_count - 1; j++) {
                dept->employee_ids[j] = dept->employee_ids[j + 1];
            }
            dept->employee_count--;
            return 0;
        }
    }

    printf("WARNING: Employee %d not found in department %s\n",
           employee_id, dept->name);
    return -1;
}
