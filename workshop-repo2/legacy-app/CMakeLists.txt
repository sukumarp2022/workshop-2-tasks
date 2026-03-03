cmake_minimum_required(VERSION 3.10)
project(EmployeeManagementSystem VERSION 2.1.3 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Source files
set(SOURCES
    src/main.cpp
    src/Employee.cpp
    src/Department.cpp
    src/PayrollRecord.cpp
    src/FileStorage.cpp
    src/ReportGenerator.cpp
)

# Include directories
include_directories(include)

# Build executable
add_executable(employee_mgmt ${SOURCES})

# Platform-specific settings
if(WIN32)
    target_compile_definitions(employee_mgmt PRIVATE _CRT_SECURE_NO_WARNINGS)
endif()
