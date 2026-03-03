// Employee.h - Legacy Employee Model
// Written in C++98/03 style with manual memory management
#ifndef EMPLOYEE_H
#define EMPLOYEE_H

#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#define MAX_NAME_LEN 100
#define MAX_EMAIL_LEN 150
#define MAX_DEPT_LEN 50

struct Employee {
    int id;
    char name[MAX_NAME_LEN];
    char email[MAX_EMAIL_LEN];
    char department[MAX_DEPT_LEN];
    double salary;
    int is_active;  // 1 = active, 0 = inactive (using int as bool)
    char hire_date[11]; // "YYYY-MM-DD\0"
};

// Allocate a new employee on the heap
Employee* create_employee(int id, const char* name, const char* email,
                          const char* department, double salary, const char* hire_date);

// Free employee memory
void destroy_employee(Employee* emp);

// Deep copy an employee
Employee* clone_employee(const Employee* emp);

// Print employee details to stdout
void print_employee(const Employee* emp);

// Validate employee data (returns 0 on success, error code on failure)
int validate_employee(const Employee* emp);

#endif // EMPLOYEE_H
