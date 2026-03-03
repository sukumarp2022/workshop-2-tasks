// PayrollRecord.h - Legacy Payroll Record
#ifndef PAYROLL_RECORD_H
#define PAYROLL_RECORD_H

#include <stdio.h>
#include <string.h>

#define MAX_PAYROLL_NOTES 500

struct PayrollRecord {
    int id;
    int employee_id;
    char pay_period[8]; // "YYYY-MM\0"
    double base_salary;
    double overtime_hours;
    double overtime_rate;
    double deductions;
    double bonus;
    double net_pay; // calculated field
    char notes[MAX_PAYROLL_NOTES];
    int is_processed; // 0 = pending, 1 = processed
};

PayrollRecord* create_payroll_record(int id, int employee_id, const char* pay_period,
                                     double base_salary, double overtime_hours,
                                     double overtime_rate, double deductions, double bonus);

void destroy_payroll_record(PayrollRecord* record);
void print_payroll_record(const PayrollRecord* record);

// Calculate net pay: base + (overtime_hours * overtime_rate) + bonus - deductions
double calculate_net_pay(PayrollRecord* record);

#endif // PAYROLL_RECORD_H
