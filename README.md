# Mamikos Backend - Spring Boot

Production-ready Spring Boot backend for the Mamikos Technical Test.

## Features & Requirements Implemented
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

## Prerequisites
- Java 21+
- Maven 3.8+
- PostgreSQL Database

---

## Configuration
Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mamikos_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

---

## API Endpoints Documentation

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
