// FileStorage.h - Legacy flat-file based storage
// Stores all data in CSV-like text files - no database
#ifndef FILE_STORAGE_H
#define FILE_STORAGE_H

#include "Employee.h"
#include "Department.h"
#include "PayrollRecord.h"

#define EMPLOYEE_FILE "data/employees.dat"
#define DEPARTMENT_FILE "data/departments.dat"
#define PAYROLL_FILE "data/payroll.dat"
#define MAX_RECORDS 1000

// Employee file operations
int save_employee_to_file(const Employee* emp);
int load_all_employees(Employee** employees, int* count);
int update_employee_in_file(const Employee* emp);
int delete_employee_from_file(int employee_id);
Employee* find_employee_by_id(int employee_id);
int find_employees_by_department(const char* department, Employee** results, int* count);

// Department file operations
int save_department_to_file(const Department* dept);
int load_all_departments(Department** departments, int* count);
Department* find_department_by_id(int department_id);

// Payroll file operations
int save_payroll_record_to_file(const PayrollRecord* record);
int load_payroll_by_employee(int employee_id, PayrollRecord** records, int* count);
int process_payroll_batch(const char* pay_period);

// Utility
int get_next_id(const char* filename);
int file_exists(const char* filename);
void ensure_data_directory();

#endif // FILE_STORAGE_H
