// ReportGenerator.h - Legacy report generation
#ifndef REPORT_GENERATOR_H
#define REPORT_GENERATOR_H

#include "Employee.h"
#include "Department.h"
#include "PayrollRecord.h"

// Generate reports to stdout or file
void generate_employee_report(const char* output_file);
void generate_department_summary(const char* output_file);
void generate_payroll_report(const char* pay_period, const char* output_file);
void generate_salary_distribution_report(const char* output_file);

// Utility: format currency string into buffer
void format_currency(double amount, char* buffer, int buffer_size);

// Utility: calculate department statistics
void calculate_dept_stats(const char* department, double* avg_salary,
                          double* total_salary, int* head_count);

#endif // REPORT_GENERATOR_H
