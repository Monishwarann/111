# 🏢 HRMS NEXUS - Enterprise Human Resource Management System

![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.3.5-brightgreen?style=for-the-badge&logo=springboot)
![Java 17](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)

**HRMS NEXUS** is a full-stack Enterprise Human Resource Management System built with **Spring Boot 3.3.5**, **Spring Data JPA**, **Spring Security**, **MySQL 8.0**, and a modern, responsive Single Page Application (SPA) frontend.

---

## 🌟 Key Features

### 📊 Executive Dashboard & Real-Time Analytics
- **Live Metrics**: Total Employees, Active Staff, Present Today, Absent Today, Late Arrivals, Pending Leaves, and Pending Payroll.
- **Top Bar Punch Clock**: Header widget with live clock and one-click **Punch In / Punch Out**.
- **Dark & Light Mode**: Instant theme switcher with browser preference memory.

### 👥 Advanced Employee Directory
- **Full Employee Profiles**: Complete profile inspection modal (Contact details, Department, Designation, Salary, Date of Joining, Emergency Contacts).
- **Instant Search & Filters**: Live filter by status (`ACTIVE` / `INACTIVE`) and text search across code, name, email, or department.
- **Directory CSV Export**: One-click export of employee records to `.csv` format.

### ⏱️ Time & Attendance Tracking
- **Automated Duration Calculation**: Standard working hours vs. late arrivals and half-day detection.
- **Status Badges**: Color-coded indicators (`PRESENT`, `ABSENT`, `LATE`, `HALF_DAY`).

### 🏖️ Leave Management & Workflow
- **Horizontal Leave Balance Cards**: Sleek 4-column horizontal card display for:
  - 🟣 **Casual Leave** (12 Days Left)
  - 🟢 **Sick Leave** (10 Days Left)
  - 🔵 **Annual Leave** (15 Days Left)
  - 🟡 **Unpaid Leave** (0 Days Used)
- **Role-Based Approvals**: HR/Admin approval & rejection workflow with custom review notes.

### 💰 Payroll Ledger & Batch Generation
- **Interactive Printable Payslips**: HTML modal preview detailing Basic Salary, Allowances (HRA), Deductions (Tax/PF), and Net Salary.
- **Batch Monthly Payroll**: Generate monthly payroll entries for all active staff in one click (`POST /api/payroll/batch`).

### ⭐ Performance & Reviews
- Visual KPI rating system (1.0 to 5.0 stars) with goals tracking and manager review history.

---

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.3.5
- **Programming Language**: Java 17
- **Database ORM**: Spring Data JPA / Hibernate
- **Database Engine**: MySQL 8.0 (HikariCP connection pool)
- **Security**: Spring Security (Role-based access control: `ADMIN`, `HR`, `EMPLOYEE`)
- **Frontend Architecture**: Vanilla HTML5, Custom CSS3 (Glassmorphism & HSL CSS Variables), ES6 Vanilla JavaScript SPA

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java JDK 17+**
- **Apache Maven 3.9+**
- **MySQL Server 8.0+** running on `localhost:3306`

### Database Setup
1. Create database in MySQL:
   ```sql
   CREATE DATABASE IF NOT EXISTS hrms_db;
   ```
2. Verify database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hrms_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=Monish@12
   ```

### Running the Application

1. Open PowerShell / Terminal in the project root:
   ```powershell
   $env:JAVA_HOME="C:\Users\kmoni\AppData\Local\Java\jdk-17.0.11+9"
   $env:PATH="$env:JAVA_HOME\bin;C:\Users\kmoni\AppData\Local\apache-maven-3.9.6\bin;$env:PATH"
   mvn spring-boot:run
   ```

2. Open your browser and navigate to:
   ```text
   http://localhost:8080
   ```

---

## 🔑 Quick Demo Login Credentials

| Role | Username | Password | Access Rights |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Full System Access, Employee Management, Batch Payroll |
| **HR Manager** | `hr` | `hr123` | Manage Employees, Leave Approvals, Performance Reviews |
| **Employee** | `employee` | `employee123` | Self Attendance Punch, Leave Application, Personal Payslips |

---

## 🔌 Core REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate user & start session |
| `GET` | `/api/dashboard/summary` | Fetch dashboard KPI counts & recent activity |
| `GET` | `/api/employees` | List all employees (filterable by status) |
| `POST` | `/api/attendance` | Mark employee punch-in attendance |
| `PUT` | `/api/leaves/{id}/approve` | Approve leave request |
| `POST` | `/api/payroll/batch` | Batch generate monthly payroll |
| `GET` | `/api/payroll/{id}` | Get payroll details for printable payslip |

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.
