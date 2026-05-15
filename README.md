# 🐝 BudgetBee - Personal Expense Tracker

BudgetBee is a full-stack web application for tracking personal expenses with a beautiful bee-themed design. Built with Spring Boot, MySQL, and vanilla JavaScript.

## ✨ Features

- **Public Dashboard** - Anyone can view the homepage with demo analytics (no login required)
- **User Authentication** - Register, Login, Logout with JWT + BCrypt
- **Expense Management** - Add, Edit, Delete expenses with categories
- **Dashboard** - Summary cards, monthly budget tracking, progress bar
- **Analytics** - Monthly trend line chart & category doughnut chart (Chart.js)
- **Reports** - Download PDF and CSV monthly reports
- **Budget Alerts** - Visual warning when spending exceeds budget
- **Dark Mode** - Toggle between light and dark themes
- **Responsive** - Works on desktop, tablet, and mobile

## 🛠 Tech Stack

| Layer    | Technology                     |
|----------|--------------------------------|
| Frontend | HTML5, CSS3, Vanilla JavaScript|
| Backend  | Java 17, Spring Boot 3.2       |
| Database | MySQL                          |
| Auth     | JWT, BCrypt                    |
| Charts   | Chart.js                       |
| PDF      | Apache PDFBox                  |
| CSV      | OpenCSV                        |

## 📁 Project Structure

```
BudgetBee-App/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/budgetbee/
│   │   │   ├── BudgetBeeApplication.java          # Entry point
│   │   │   ├── config/
│   │   │   │   ├── AppUserDetailsService.java      # Loads users for Spring Security
│   │   │   │   ├── CorsConfig.java                 # CORS configuration
│   │   │   │   ├── JwtAuthFilter.java              # JWT request filter
│   │   │   │   ├── JwtUtil.java                    # JWT token utilities
│   │   │   │   └── SecurityConfig.java             # Spring Security config
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java             # Login/Register endpoints
│   │   │   │   ├── DashboardController.java        # Dashboard data + budget
│   │   │   │   ├── ExpenseController.java          # CRUD expenses
│   │   │   │   ├── ReportController.java           # PDF/CSV download
│   │   │   │   └── UserController.java             # Profile update
│   │   │   ├── dto/
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── DashboardDTO.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── RegisterRequest.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java     # Error handling
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── Expense.java                    # Expense entity
│   │   │   │   ├── MonthlyBudget.java              # Budget entity
│   │   │   │   └── User.java                       # User entity
│   │   │   ├── repository/
│   │   │   │   ├── ExpenseRepository.java          # Expense queries
│   │   │   │   ├── MonthlyBudgetRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       ├── DashboardService.java           # Dashboard logic
│   │   │       ├── ExpenseService.java             # Expense CRUD logic
│   │   │       ├── ReportService.java              # PDF/CSV generation
│   │   │       └── UserService.java                # Auth + profile logic
│   │   └── resources/
│   │       ├── application.properties              # Config
│   │       ├── schema.sql                          # DB schema
│   │       └── static/
│   │           ├── index.html                      # Main frontend page
│   │           ├── css/style.css                   # All styles
│   │           └── js/
│   │               ├── api.js                      # API helper
│   │               ├── app.js                      # App init + navigation
│   │               ├── auth.js                     # Auth UI logic
│   │               ├── analytics.js                # Charts + reports
│   │               ├── dashboard.js                # Dashboard data
│   │               └── expenses.js                 # Expense CRUD UI
│   └── test/java/com/budgetbee/
└── README.md
```

## 🌐 API Reference

### Public Endpoints
| Method | Endpoint                | Description          |
|--------|-------------------------|----------------------|
| GET    | /api/dashboard/public   | Get demo dashboard   |

### Auth Endpoints (public)
| Method | Endpoint             | Description      |
|--------|----------------------|------------------|
| POST   | /api/auth/register   | Register new user|
| POST   | /api/auth/login      | Login user       |

### Protected Endpoints (JWT required)
| Method | Endpoint                    | Description            |
|--------|-----------------------------|------------------------|
| GET    | /api/auth/me                | Get current user       |
| GET    | /api/dashboard              | Get user dashboard     |
| GET    | /api/dashboard/budget       | Get monthly budget     |
| POST   | /api/dashboard/budget       | Set monthly budget     |
| GET    | /api/dashboard/budget-alert | Check budget exceeded  |
| GET    | /api/expenses               | List expenses (filter) |
| GET    | /api/expenses/search?q=     | Search expenses        |
| POST   | /api/expenses               | Add expense            |
| PUT    | /api/expenses/{id}          | Update expense         |
| DELETE | /api/expenses/{id}          | Delete expense         |
| GET    | /api/reports/pdf            | Download PDF report    |
| GET    | /api/reports/csv            | Download CSV report    |
| PUT    | /api/users/profile          | Update profile         |

## 🚀 Step-by-Step Setup

### Prerequisites
- Java 17 or later
- Maven 3.8+
- MySQL 8.0+
- Git (optional)

### 1. Create Database
Open MySQL and run:
```sql
CREATE DATABASE budgetbee_db;
```

### 2. Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/budgetbee_db
spring.datasource.username=root       # Change to your MySQL username
spring.datasource.password=root       # Change to your MySQL password
```

### 3. Build and Run
```bash
cd BudgetBee-App
mvn clean package
mvn spring-boot:run
```

The app starts at **http://localhost:8080**

### 4. Verify
- Open browser at http://localhost:8080
- The homepage loads instantly with demo data
- Click Register to create an account
- Login and start tracking expenses

## 🐝 How It Works

### Public Mode
When you open the site, the dashboard shows sample data (demo analytics, sample expenses). The navbar shows Login and Register buttons. This allows anyone to immediately see what the app looks like.

### After Login
Once logged in, all demo data is replaced with your actual data from MySQL. You can:
- Add, edit, delete expenses
- Set a monthly budget and track spending
- View charts (monthly trend + category breakdown)
- Download PDF/CSV reports
- Update your profile

### Database Tables
- **users** - Stores registered users
- **expenses** - Stores expenses linked to users via `user_id`
- **monthly_budgets** - Stores monthly budget per user

## 👨‍💻 For Internship / Portfolio
This project demonstrates:
- Full-stack development (Java + Spring Boot + MySQL + JS)
- REST API design and implementation
- JWT authentication and security
- PDF/CSV report generation
- Chart.js data visualization
- Responsive UI design
- Clean architecture (Controller → Service → Repository)

## 📝 License
MIT
