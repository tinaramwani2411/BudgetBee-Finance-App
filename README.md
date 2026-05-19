# 💰 BudgetBee - Personal Finance Tracker

BudgetBee is a full-stack web application that helps users manage their expenses and monthly budgets efficiently. It provides secure authentication and real-time financial tracking.

---

## 🚀 Live Demo
👉 https://budgetbee-deploy-test.onrender.com/#

---

## 🛠️ Tech Stack

### Backend:
- Java 17+
- Spring Boot
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- REST APIs

### Database:
- MySQL (Aiven Cloud)

### Deployment:
- Render (Docker-based deployment)

---

## ✨ Features

- 🔐 User Registration & Login (JWT Authentication)
- 💸 Add / Update / Delete Expenses
- 📊 Monthly Budget Management
- 📈 Expense Tracking by Category
- 🗄️ Cloud Database Integration
- 🌐 Fully Deployed Backend API

---

## 🏗️ Project Structure
src/main/java/com/budgetbee
├── controller
├── service
├── repository
├── model
├── config

## ⚙️ Environment Variables (Render)

Configure these in Render:
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD

## ▶️ How to Run Locally

```bash
git clone https://github.com/your-username/BudgetBee.git
cd BudgetBee
mvn clean install
mvn spring-boot:run

📌 API Endpoints (Example)
POST   /api/auth/register
POST   /api/auth/login
GET    /api/expenses
POST   /api/expenses
DELETE /api/expenses/{id}

👩‍💻 Author
Tina Ramwani
GitHub: https://github.com/tinaramwani2411
⭐ Future Improvements
Frontend React integration
Advanced analytics dashboard
Notifications & reminders
Mobile app version

📄 License

This project is for educational/demo purposes.
