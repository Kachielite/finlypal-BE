# FinlyPal - AI-Powered Expense Tracker

FinlyPal is an AI-powered expense tracking app that helps users manage their finances, analyze spending habits, and receive personalized recommendations for saving money. Built with **Spring Boot** for the backend and **Flutter** for the frontend, SpendWise combines simplicity with powerful AI-driven insights.

## API Documentation (MVP)

Access the full API documentation on Swagger UI: [here](https://finlypal.onrender.com/api/v1/swagger-ui/index.html)

---

## Features

### **V1 (MVP)**
- **User Authentication**: Register and log in securely with JWT-based authentication.
- **Expense Management**: Add, edit, delete, and view expenses with details like amount, category, date, and description.
- **Basic Insights**: View total spending and spending breakdown by category.
- **RESTful APIs**: Fully documented APIs for seamless integration with the Flutter frontend.

### **V2 (Planned)**
- **AI Integration**: Use OpenAI to categorize expenses and provide personalized recommendations.
- **Budget Management**: Set monthly budgets and receive alerts when limits are exceeded.
- **Advanced Analytics**: Compare spending month-over-month and identify trends.

### **V3 (Future)**
- **Goal Setting**: Set and track financial goals (e.g., saving for a vacation).
- **Recurring Expenses**: Manage recurring payments like rent and subscriptions.
- **Data Export**: Export expense data as CSV or PDF.
- **Notifications**: Receive push notifications for budgets, goals, and recurring expenses.

---

## Tech Stack

### **Backend**
- **Framework**: Spring Boot
- **Database**: Postgresql
- **Authentication**: JWT (JSON Web Tokens)
- **API Documentation**: Swagger/OpenAPI
- **AI Integration**: OpenAI API

---

## Getting Started

### Prerequisites
- Java 17+ (for Spring Boot)
- Postgres 16.0+
- OpenAI API Key

### Installation

#### Backend Setup:
 
1. Update the application.properties file with your database credentials:

    ```bash
    spring.datasource.url=jdbc:mysql://localhost:3306/spendwise_db
    spring.datasource.username=root
    spring.datasource.password=yourpassword

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
