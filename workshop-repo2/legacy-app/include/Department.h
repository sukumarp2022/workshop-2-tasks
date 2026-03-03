// Department.h - Legacy Department Model
#ifndef DEPARTMENT_H
#define DEPARTMENT_H

#include <string.h>
#include <stdio.h>

#define MAX_DEPT_NAME 50
#define MAX_DEPT_DESC 200
#define MAX_EMPLOYEES_PER_DEPT 100

struct Department {
    int id;
    char name[MAX_DEPT_NAME];
    char description[MAX_DEPT_DESC];
    int manager_id;
    double budget;
    int employee_count;
    int employee_ids[MAX_EMPLOYEES_PER_DEPT]; // fixed-size array of employee IDs
};

Department* create_department(int id, const char* name, const char* description,
                              int manager_id, double budget);

void destroy_department(Department* dept);

void print_department(const Department* dept);

// Add employee ID to department (returns 0 on success, -1 if full)
int department_add_employee(Department* dept, int employee_id);

// Remove employee ID from department (returns 0 on success, -1 if not found)
int department_remove_employee(Department* dept, int employee_id);

#endif // DEPARTMENT_H
