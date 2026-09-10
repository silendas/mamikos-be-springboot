# Mamikos Backend - Spring Boot

Production-ready Spring Boot backend for the Mamikos Technical Test.

---

## 🛠️ Tech Stack & Versions

| Technology | Version | Description |
| :--- | :--- | :--- |
| **Java** | `21` | Programming Language |
| **Spring Boot** | `3.3.0` | Backend Framework |
| **Spring Security** | `6.3.0` | Authentication & Authorization |
| **Spring Data JPA** | `3.3.0` | ORM / Database Access |
| **JJWT (JSON Web Token)** | `0.12.5` | Token-based Auth |
| **SpringDoc OpenAPI** | `2.5.0` | API Documentation & Swagger UI |
| **PostgreSQL** | Latest | Relational Database |
| **Maven** | `3.8+` | Build Tool & Dependency Management |

---

## 📋 Features & Requirements Implemented
1. **User Roles & Credits**:
   - `REGULAR_USER`: Gets 20 initial credits.
   - `PREMIUM_USER`: Gets 40 initial credits.
   - `OWNER`: Gets 0 credits. Can add, update, delete, and view their own kosts.
2. **Kost Management (Owner)**:
   - Create, update, delete kosts.
   - View owner kost list.
3. **Kost Search & Filter (Public/User)**:
   - Search by name, location, price range (`minPrice`, `maxPrice`).
   - Sort results by price (`sort=asc` or `sort=desc`).
   - View kost detail.
4. **Room Availability Inquiry**:
   - Users can ask about room availability (`-5 credits` per inquiry).
   - Validates sufficient credits.
5. **Scheduled Task**:
   - Monthly credit recharge on the 1st of every month (`@Scheduled` cron task).
6. **Architecture & Clean Code**:
   - Modular structure: `common`, `controller`, `service`, `repository`, `model`, `dto`, `security`, `exception`, `schedule`.
   - Global exception handling (`@ControllerAdvice`) with standard `BaseResponse`.
   - Strict Git commit convention for every change.

---

## ⚙️ Prerequisites
- **Java JDK 21** installed (`java -version`)
- **Maven 3.8+** (or use included Maven Wrapper `mvnw`)
- **PostgreSQL Database** running locally or remotely

---

## 🚀 Step-by-Step Installation & Running Guide

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/mamikos-be-springboot.git
cd mamikos-be-springboot
```

### 2. Setup PostgreSQL Database
Create a PostgreSQL database named `mamikos_db`:
```sql
CREATE DATABASE mamikos_db;
```

### 3. Configure Database Credentials
Edit `src/main/resources/application.properties` (or profile configuration) to match your local PostgreSQL credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mamikos_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 4. Build the Application
Using Maven:
```bash
mvn clean install
```

### 5. Run the Application
```bash
# Windows
mvn spring-boot:run

# Linux / macOS
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

---

## 📖 API Documentation & Swagger UI
Interactive API documentation is available via Swagger UI once the application is running:
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 🧪 API Endpoints Reference

### 1. Auth API (`/api/auth`)
- **Register**: `POST /api/auth/register`
  ```json
  {
    "username": "budi_owner",
    "email": "budi@owner.com",
    "password": "password123",
    "role": "OWNER"
  }
  ```
- **Login**: `POST /api/auth/login`
  ```json
  {
    "usernameOrEmail": "budi_owner",
    "password": "password123"
  }
  ```

### 2. Kost API (`/api/kosts`)
- **Create Kost (Owner)**: `POST /api/kosts` (Requires Bearer Token)
  ```json
  {
    "name": "Kost Melati Indah",
    "location": "Jakarta Selatan",
    "price": 1500000.0,
    "description": "Kost nyaman dekat stasiun",
    "roomCount": 10
  }
  ```
- **Search Kost (Public)**: `GET /api/kosts/search?location=Jakarta&sort=asc`
- **Kost Detail (Public)**: `GET /api/kosts/{id}`
- **Owner Kosts**: `GET /api/kosts/owner/my-kosts` (Requires Owner Token)
- **Update Kost**: `PUT /api/kosts/{id}` (Owner)
- **Delete Kost**: `DELETE /api/kosts/{id}` (Owner)

### 3. Inquiry API (`/api/inquiries`)
- **Ask Room Availability (-5 credits)**: `POST /api/inquiries` (Requires Regular/Premium User Token)
  ```json
  {
    "kostId": 1,
    "message": "Apakah kamar masih tersedia untuk bulan depan?"
  }
  ```
- **User Inquiries**: `GET /api/inquiries/my-inquiries`

