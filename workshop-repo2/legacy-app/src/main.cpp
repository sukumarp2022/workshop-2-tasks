// main.cpp - Legacy Employee Management System
// Console-based menu-driven application
// Typical of enterprise C/C++ applications from the early 2000s
#include "../include/Employee.h"
#include "../include/Department.h"
#include "../include/PayrollRecord.h"
#include "../include/FileStorage.h"
#include "../include/ReportGenerator.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Application version - hardcoded
#define APP_VERSION "2.1.3"
#define APP_NAME "Employee Management System"

// Global state (anti-pattern)
static int g_is_running = 1;
static int g_current_user_role = 0; // 0=admin, 1=manager, 2=viewer
static char g_current_user[MAX_NAME_LEN] = "admin";

void clear_input_buffer() {
    int c;
    while ((c = getchar()) != '\n' && c != EOF);
}

void print_menu() {
    printf("\n");
    printf("============================================\n");
    printf("  %s v%s\n", APP_NAME, APP_VERSION);
    printf("  Logged in as: %s\n", g_current_user);
    printf("============================================\n");
    printf("  1. Add Employee\n");
    printf("  2. View All Employees\n");
    printf("  3. Search Employee by ID\n");
    printf("  4. Update Employee\n");
    printf("  5. Delete Employee\n");
    printf("  6. Add Department\n");
    printf("  7. View Departments\n");
    printf("  8. Create Payroll Record\n");
    printf("  9. Process Payroll\n");
    printf(" 10. Employee Report\n");
    printf(" 11. Department Summary\n");
    printf(" 12. Payroll Report\n");
    printf(" 13. Salary Distribution\n");
    printf("  0. Exit\n");
    printf("============================================\n");
    printf("Enter choice: ");
}

void handle_add_employee() {
    char name[MAX_NAME_LEN], email[MAX_EMAIL_LEN];
    char department[MAX_DEPT_LEN], hire_date[11];
    double salary;

    printf("\n--- Add New Employee ---\n");

    printf("Name: ");
    fgets(name, MAX_NAME_LEN, stdin);
    name[strcspn(name, "\n")] = '\0';

    printf("Email: ");
    fgets(email, MAX_EMAIL_LEN, stdin);
    email[strcspn(email, "\n")] = '\0';

    printf("Department: ");
    fgets(department, MAX_DEPT_LEN, stdin);
    department[strcspn(department, "\n")] = '\0';

    printf("Salary: ");
    scanf("%lf", &salary);
    clear_input_buffer();

    printf("Hire Date (YYYY-MM-DD): ");
    fgets(hire_date, 11, stdin);
    hire_date[strcspn(hire_date, "\n")] = '\0';
    clear_input_buffer();

    int id = get_next_id(EMPLOYEE_FILE);
    Employee* emp = create_employee(id, name, email, department, salary, hire_date);

    if (emp == NULL) {
        printf("ERROR: Failed to create employee\n");
        return;
    }

    int validation = validate_employee(emp);
    if (validation != 0) {
        printf("ERROR: Employee validation failed (code: %d)\n", validation);
        destroy_employee(emp);
        return;
    }

    if (save_employee_to_file(emp) == 0) {
        printf("SUCCESS: Employee '%s' added with ID %d\n", emp->name, emp->id);
    } else {
        printf("ERROR: Failed to save employee to file\n");
    }

    destroy_employee(emp);
}

void handle_view_all_employees() {
    Employee* employees = NULL;
    int count = 0;

    if (load_all_employees(&employees, &count) != 0) {
        printf("ERROR: Failed to load employees\n");
        return;
    }

    if (count == 0) {
        printf("\nNo employees found.\n");
        return;
    }

    printf("\n--- All Employees (%d) ---\n", count);
    for (int i = 0; i < count; i++) {
        print_employee(&employees[i]);
    }

    free(employees);
}

void handle_search_employee() {
    int id;
    printf("\nEnter Employee ID: ");
    scanf("%d", &id);
    clear_input_buffer();

    Employee* emp = find_employee_by_id(id);
    if (emp == NULL) {
        printf("Employee with ID %d not found.\n", id);
        return;
    }

    print_employee(emp);
    destroy_employee(emp);
}

void handle_update_employee() {
    int id;
    printf("\nEnter Employee ID to update: ");
    scanf("%d", &id);
    clear_input_buffer();

    Employee* emp = find_employee_by_id(id);
    if (emp == NULL) {
        printf("Employee with ID %d not found.\n", id);
        return;
    }

    printf("Current details:\n");
    print_employee(emp);

    char input[MAX_NAME_LEN];

    printf("New Name (press Enter to keep '%s'): ", emp->name);
    fgets(input, MAX_NAME_LEN, stdin);
    input[strcspn(input, "\n")] = '\0';
    if (strlen(input) > 0) strncpy(emp->name, input, MAX_NAME_LEN - 1);

    printf("New Email (press Enter to keep '%s'): ", emp->email);
    fgets(input, MAX_EMAIL_LEN, stdin);
    input[strcspn(input, "\n")] = '\0';
    if (strlen(input) > 0) strncpy(emp->email, input, MAX_EMAIL_LEN - 1);

    printf("New Department (press Enter to keep '%s'): ", emp->department);
    fgets(input, MAX_DEPT_LEN, stdin);
    input[strcspn(input, "\n")] = '\0';
    if (strlen(input) > 0) strncpy(emp->department, input, MAX_DEPT_LEN - 1);

    printf("New Salary (enter 0 to keep $%.2f): ", emp->salary);
    double new_salary;
    scanf("%lf", &new_salary);
    clear_input_buffer();
    if (new_salary > 0) emp->salary = new_salary;

    printf("Active status (1=active, 0=inactive, -1=keep): ");
    int status;
    scanf("%d", &status);
    clear_input_buffer();
    if (status >= 0) emp->is_active = status;

    if (update_employee_in_file(emp) == 0) {
        printf("SUCCESS: Employee %d updated.\n", id);
    } else {
        printf("ERROR: Failed to update employee.\n");
    }

    destroy_employee(emp);
}

void handle_delete_employee() {
    int id;
    printf("\nEnter Employee ID to delete: ");
    scanf("%d", &id);
    clear_input_buffer();

    // No confirmation prompt - risky!
    if (delete_employee_from_file(id) == 0) {
        printf("SUCCESS: Employee %d deleted.\n", id);
    } else {
        printf("ERROR: Employee %d not found or delete failed.\n", id);
    }
}

void handle_add_department() {
    char name[MAX_DEPT_NAME], description[MAX_DEPT_DESC];
    int manager_id;
    double budget;

    printf("\n--- Add New Department ---\n");

    printf("Department Name: ");
    fgets(name, MAX_DEPT_NAME, stdin);
    name[strcspn(name, "\n")] = '\0';

    printf("Description: ");
    fgets(description, MAX_DEPT_DESC, stdin);
    description[strcspn(description, "\n")] = '\0';

    printf("Manager Employee ID: ");
    scanf("%d", &manager_id);
    clear_input_buffer();

    printf("Budget: ");
    scanf("%lf", &budget);
    clear_input_buffer();

    int id = get_next_id(DEPARTMENT_FILE);
    Department* dept = create_department(id, name, description, manager_id, budget);

    if (dept == NULL) {
        printf("ERROR: Failed to create department\n");
        return;
    }

    if (save_department_to_file(dept) == 0) {
        printf("SUCCESS: Department '%s' added with ID %d\n", dept->name, dept->id);
    } else {
        printf("ERROR: Failed to save department to file\n");
    }

    destroy_department(dept);
}

void handle_view_departments() {
    Department* departments = NULL;
    int count = 0;

    if (load_all_departments(&departments, &count) != 0) {
        printf("ERROR: Failed to load departments\n");
        return;
    }

    if (count == 0) {
        printf("\nNo departments found.\n");
        return;
    }

    printf("\n--- All Departments (%d) ---\n", count);
    for (int i = 0; i < count; i++) {
        print_department(&departments[i]);
    }

    free(departments);
}

void handle_create_payroll() {
    int employee_id;
    char pay_period[8];
    double base_salary, overtime_hours, overtime_rate, deductions, bonus;

    printf("\n--- Create Payroll Record ---\n");

    printf("Employee ID: ");
    scanf("%d", &employee_id);
    clear_input_buffer();

    // Verify employee exists
    Employee* emp = find_employee_by_id(employee_id);
    if (emp == NULL) {
        printf("ERROR: Employee %d not found.\n", employee_id);
        return;
    }
    base_salary = emp->salary / 12.0; // Monthly base from annual salary
    destroy_employee(emp);

    printf("Pay Period (YYYY-MM): ");
    fgets(pay_period, 8, stdin);
    pay_period[strcspn(pay_period, "\n")] = '\0';
    clear_input_buffer();

    printf("Overtime Hours: ");
    scanf("%lf", &overtime_hours);
    printf("Overtime Rate ($/hr): ");
    scanf("%lf", &overtime_rate);
    printf("Deductions: ");
    scanf("%lf", &deductions);
    printf("Bonus: ");
    scanf("%lf", &bonus);
    clear_input_buffer();

    int id = get_next_id(PAYROLL_FILE);
    PayrollRecord* record = create_payroll_record(id, employee_id, pay_period,
                                                   base_salary, overtime_hours,
                                                   overtime_rate, deductions, bonus);

    if (record == NULL) {
        printf("ERROR: Failed to create payroll record\n");
        return;
    }

    calculate_net_pay(record);

    if (save_payroll_record_to_file(record) == 0) {
        printf("SUCCESS: Payroll record created. Net pay: $%.2f\n", record->net_pay);
    } else {
        printf("ERROR: Failed to save payroll record.\n");
    }

    destroy_payroll_record(record);
}

void handle_process_payroll() {
    char pay_period[8];

    printf("\nEnter Pay Period to process (YYYY-MM): ");
    fgets(pay_period, 8, stdin);
    pay_period[strcspn(pay_period, "\n")] = '\0';
    clear_input_buffer();

    int result = process_payroll_batch(pay_period);
    if (result >= 0) {
        printf("Payroll processing complete. %d records processed.\n", result);
    } else {
        printf("ERROR: Payroll processing failed.\n");
    }
}

void handle_reports(int report_type) {
    char output_file[256];
    printf("Output to file? (Enter filename or press Enter for screen): ");
    fgets(output_file, sizeof(output_file), stdin);
    output_file[strcspn(output_file, "\n")] = '\0';

    const char* file = (strlen(output_file) > 0) ? output_file : NULL;

    switch (report_type) {
        case 10:
            generate_employee_report(file);
            break;
        case 11:
            generate_department_summary(file);
            break;
        case 12: {
            char period[8];
            printf("Pay Period (YYYY-MM): ");
            fgets(period, 8, stdin);
            period[strcspn(period, "\n")] = '\0';
            clear_input_buffer();
            generate_payroll_report(period, file);
            break;
        }
        case 13:
            generate_salary_distribution_report(file);
            break;
        default:
            printf("Unknown report type\n");
    }
}

int main() {
    printf("Starting %s v%s...\n", APP_NAME, APP_VERSION);
    ensure_data_directory();

    int choice;

    while (g_is_running) {
        print_menu();

        if (scanf("%d", &choice) != 1) {
            printf("Invalid input. Please enter a number.\n");
            clear_input_buffer();
            continue;
        }
        clear_input_buffer();

        switch (choice) {
            case 1: handle_add_employee(); break;
            case 2: handle_view_all_employees(); break;
            case 3: handle_search_employee(); break;
            case 4: handle_update_employee(); break;
            case 5: handle_delete_employee(); break;
            case 6: handle_add_department(); break;
            case 7: handle_view_departments(); break;
            case 8: handle_create_payroll(); break;
            case 9: handle_process_payroll(); break;
            case 10:
            case 11:
            case 12:
            case 13:
                handle_reports(choice);
                break;
            case 0:
                g_is_running = 0;
                printf("Goodbye!\n");
                break;
            default:
                printf("Invalid choice. Try again.\n");
        }
    }

    return 0;
}
