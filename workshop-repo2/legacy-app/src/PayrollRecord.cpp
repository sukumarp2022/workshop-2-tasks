// PayrollRecord.cpp - Payroll record implementation
#include "../include/PayrollRecord.h"
#include <stdlib.h>

PayrollRecord* create_payroll_record(int id, int employee_id, const char* pay_period,
                                     double base_salary, double overtime_hours,
                                     double overtime_rate, double deductions, double bonus) {
    PayrollRecord* record = (PayrollRecord*)malloc(sizeof(PayrollRecord));
    if (record == NULL) {
        printf("ERROR: Failed to allocate memory for payroll record\n");
        return NULL;
    }

    record->id = id;
    record->employee_id = employee_id;
    strncpy(record->pay_period, pay_period, 7);
    record->pay_period[7] = '\0';
    record->base_salary = base_salary;
    record->overtime_hours = overtime_hours;
    record->overtime_rate = overtime_rate;
    record->deductions = deductions;
    record->bonus = bonus;
    record->net_pay = 0.0;
    record->notes[0] = '\0';
    record->is_processed = 0;

    return record;
}

void destroy_payroll_record(PayrollRecord* record) {
    if (record != NULL) {
        free(record);
    }
}

void print_payroll_record(const PayrollRecord* record) {
    if (record == NULL) {
        printf("ERROR: NULL payroll record pointer\n");
        return;
    }
    printf("========================================\n");
    printf("Payroll ID:       %d\n", record->id);
    printf("Employee ID:      %d\n", record->employee_id);
    printf("Pay Period:       %s\n", record->pay_period);
    printf("Base Salary:      $%.2f\n", record->base_salary);
    printf("Overtime:         %.1f hrs @ $%.2f/hr\n",
           record->overtime_hours, record->overtime_rate);
    printf("Bonus:            $%.2f\n", record->bonus);
    printf("Deductions:       $%.2f\n", record->deductions);
    printf("Net Pay:          $%.2f\n", record->net_pay);
    printf("Status:           %s\n", record->is_processed ? "Processed" : "Pending");
    if (strlen(record->notes) > 0) {
        printf("Notes:            %s\n", record->notes);
    }
    printf("========================================\n");
}

double calculate_net_pay(PayrollRecord* record) {
    if (record == NULL) return -1.0;

    double overtime_pay = record->overtime_hours * record->overtime_rate;
    record->net_pay = record->base_salary + overtime_pay + record->bonus - record->deductions;

    // BUG: No negative pay check - known issue, never fixed
    return record->net_pay;
}
