// ReportGenerator.cpp - Report generation with legacy patterns
// God class: handles formatting, data access, calculation, and output
#include "../include/ReportGenerator.h"
#include "../include/FileStorage.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// Hardcoded tax rates - should be configurable
#define FEDERAL_TAX_RATE 0.22
#define STATE_TAX_RATE 0.05
#define INSURANCE_DEDUCTION 250.00
#define RETIREMENT_RATE 0.06

void format_currency(double amount, char* buffer, int buffer_size) {
    if (buffer == NULL || buffer_size < 2) return;
    snprintf(buffer, buffer_size, "$%.2f", amount);
}

void calculate_dept_stats(const char* department, double* avg_salary,
                          double* total_salary, int* head_count) {
    Employee* employees = NULL;
    int count = 0;

    if (find_employees_by_department(department, &employees, &count) != 0) {
        *avg_salary = 0;
        *total_salary = 0;
        *head_count = 0;
        return;
    }

    *total_salary = 0;
    *head_count = 0;

    for (int i = 0; i < count; i++) {
        if (employees[i].is_active) {
            *total_salary += employees[i].salary;
            (*head_count)++;
        }
    }

    *avg_salary = (*head_count > 0) ? (*total_salary / *head_count) : 0;

    free(employees);
}

void generate_employee_report(const char* output_file) {
    Employee* employees = NULL;
    int count = 0;

    if (load_all_employees(&employees, &count) != 0) {
        printf("ERROR: Failed to load employees for report\n");
        return;
    }

    FILE* out = stdout;
    if (output_file != NULL) {
        out = fopen(output_file, "w");
        if (out == NULL) {
            printf("ERROR: Cannot open output file %s\n", output_file);
            free(employees);
            return;
        }
    }

    // Get current date for report header
    time_t now = time(NULL);
    char date_str[26];
    // Using ctime - deprecated in some compilers
    strcpy(date_str, ctime(&now));
    date_str[24] = '\0'; // Remove trailing newline

    fprintf(out, "=============================================\n");
    fprintf(out, "       EMPLOYEE DIRECTORY REPORT\n");
    fprintf(out, "       Generated: %s\n", date_str);
    fprintf(out, "=============================================\n\n");

    int active_count = 0;
    int inactive_count = 0;
    double total_salary = 0;

    for (int i = 0; i < count; i++) {
        fprintf(out, "ID: %d | Name: %-30s | Dept: %-15s | Salary: $%.2f | %s\n",
                employees[i].id, employees[i].name, employees[i].department,
                employees[i].salary,
                employees[i].is_active ? "ACTIVE" : "INACTIVE");

        if (employees[i].is_active) {
            active_count++;
            total_salary += employees[i].salary;
        } else {
            inactive_count++;
        }
    }

    fprintf(out, "\n---------------------------------------------\n");
    fprintf(out, "Total Employees: %d (Active: %d, Inactive: %d)\n",
            count, active_count, inactive_count);
    fprintf(out, "Total Salary Expense: $%.2f\n", total_salary);
    fprintf(out, "Average Salary: $%.2f\n",
            active_count > 0 ? total_salary / active_count : 0);
    fprintf(out, "=============================================\n");

    if (output_file != NULL) fclose(out);
    free(employees);
}

void generate_department_summary(const char* output_file) {
    Department* departments = NULL;
    int count = 0;

    if (load_all_departments(&departments, &count) != 0) {
        printf("ERROR: Failed to load departments for report\n");
        return;
    }

    FILE* out = stdout;
    if (output_file != NULL) {
        out = fopen(output_file, "w");
        if (out == NULL) {
            printf("ERROR: Cannot open output file %s\n", output_file);
            free(departments);
            return;
        }
    }

    fprintf(out, "=============================================\n");
    fprintf(out, "       DEPARTMENT SUMMARY REPORT\n");
    fprintf(out, "=============================================\n\n");

    double company_total = 0;
    int company_headcount = 0;

    for (int i = 0; i < count; i++) {
        double avg_sal, total_sal;
        int heads;
        calculate_dept_stats(departments[i].name, &avg_sal, &total_sal, &heads);

        fprintf(out, "Department: %s\n", departments[i].name);
        fprintf(out, "  Budget:       $%.2f\n", departments[i].budget);
        fprintf(out, "  Headcount:    %d\n", heads);
        fprintf(out, "  Avg Salary:   $%.2f\n", avg_sal);
        fprintf(out, "  Total Salary: $%.2f\n", total_sal);
        fprintf(out, "  Budget Used:  %.1f%%\n",
                departments[i].budget > 0 ? (total_sal / departments[i].budget) * 100 : 0);
        fprintf(out, "\n");

        company_total += total_sal;
        company_headcount += heads;
    }

    fprintf(out, "---------------------------------------------\n");
    fprintf(out, "Company Total Salary: $%.2f\n", company_total);
    fprintf(out, "Company Headcount:    %d\n", company_headcount);
    fprintf(out, "=============================================\n");

    if (output_file != NULL) fclose(out);
    free(departments);
}

void generate_payroll_report(const char* pay_period, const char* output_file) {
    Employee* employees = NULL;
    int emp_count = 0;

    if (load_all_employees(&employees, &emp_count) != 0) {
        printf("ERROR: Failed to load employees for payroll report\n");
        return;
    }

    FILE* out = stdout;
    if (output_file != NULL) {
        out = fopen(output_file, "w");
        if (out == NULL) {
            printf("ERROR: Cannot open output file %s\n", output_file);
            free(employees);
            return;
        }
    }

    fprintf(out, "=============================================\n");
    fprintf(out, "       PAYROLL REPORT - %s\n", pay_period);
    fprintf(out, "=============================================\n\n");

    double grand_total = 0;
    int records_found = 0;

    for (int i = 0; i < emp_count; i++) {
        PayrollRecord* records = NULL;
        int rec_count = 0;

        if (load_payroll_by_employee(employees[i].id, &records, &rec_count) == 0) {
            for (int j = 0; j < rec_count; j++) {
                if (strcmp(records[j].pay_period, pay_period) == 0) {
                    // Calculate taxes inline - should be a separate function
                    double federal_tax = records[j].net_pay * FEDERAL_TAX_RATE;
                    double state_tax = records[j].net_pay * STATE_TAX_RATE;
                    double retirement = records[j].net_pay * RETIREMENT_RATE;
                    double take_home = records[j].net_pay - federal_tax - state_tax
                                       - retirement - INSURANCE_DEDUCTION;

                    fprintf(out, "Employee: %s (ID: %d)\n", employees[i].name, employees[i].id);
                    fprintf(out, "  Gross Pay:    $%.2f\n", records[j].net_pay);
                    fprintf(out, "  Federal Tax:  $%.2f\n", federal_tax);
                    fprintf(out, "  State Tax:    $%.2f\n", state_tax);
                    fprintf(out, "  Retirement:   $%.2f\n", retirement);
                    fprintf(out, "  Insurance:    $%.2f\n", INSURANCE_DEDUCTION);
                    fprintf(out, "  Take Home:    $%.2f\n", take_home);
                    fprintf(out, "  Status:       %s\n\n",
                            records[j].is_processed ? "PROCESSED" : "PENDING");

                    grand_total += records[j].net_pay;
                    records_found++;
                }
            }
            free(records);
        }
    }

    fprintf(out, "---------------------------------------------\n");
    fprintf(out, "Total Records:     %d\n", records_found);
    fprintf(out, "Total Gross Pay:   $%.2f\n", grand_total);
    fprintf(out, "=============================================\n");

    if (output_file != NULL) fclose(out);
    free(employees);
}

void generate_salary_distribution_report(const char* output_file) {
    Employee* employees = NULL;
    int count = 0;

    if (load_all_employees(&employees, &count) != 0) {
        printf("ERROR: Failed to load employees for salary report\n");
        return;
    }

    FILE* out = stdout;
    if (output_file != NULL) {
        out = fopen(output_file, "w");
        if (out == NULL) {
            printf("ERROR: Cannot open output file %s\n", output_file);
            free(employees);
            return;
        }
    }

    // Hardcoded salary brackets - not configurable
    int bracket_0_50k = 0;
    int bracket_50_75k = 0;
    int bracket_75_100k = 0;
    int bracket_100_150k = 0;
    int bracket_150k_plus = 0;

    for (int i = 0; i < count; i++) {
        if (!employees[i].is_active) continue;

        double sal = employees[i].salary;
        if (sal < 50000) bracket_0_50k++;
        else if (sal < 75000) bracket_50_75k++;
        else if (sal < 100000) bracket_75_100k++;
        else if (sal < 150000) bracket_100_150k++;
        else bracket_150k_plus++;
    }

    fprintf(out, "=============================================\n");
    fprintf(out, "       SALARY DISTRIBUTION REPORT\n");
    fprintf(out, "=============================================\n\n");
    fprintf(out, "  $0 - $50K:      %d employees\n", bracket_0_50k);
    fprintf(out, "  $50K - $75K:    %d employees\n", bracket_50_75k);
    fprintf(out, "  $75K - $100K:   %d employees\n", bracket_75_100k);
    fprintf(out, "  $100K - $150K:  %d employees\n", bracket_100_150k);
    fprintf(out, "  $150K+:         %d employees\n", bracket_150k_plus);
    fprintf(out, "=============================================\n");

    if (output_file != NULL) fclose(out);
    free(employees);
}
