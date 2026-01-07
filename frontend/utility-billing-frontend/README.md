# Utility Billing System 💡💧🔥🌐

##  Project Overview
The **Utility Billing System** is a microservices-based web application designed to manage utility services such as **Electricity, Water, Gas, and Internet**.  
It automates **consumer management, meter readings, bill generation, and payments**, ensuring secure and efficient billing operations.

The system follows a **modern microservices architecture** with centralized authentication, service discovery, and API gateway.

---

##  Problem Statement
Traditional utility billing systems are often manual, slow, and error-prone.  
This project aims to provide a **centralized, automated, and secure platform** for managing utility billing operations efficiently.

---

##  Objectives
- Automate utility billing and payment processes  
- Provide role-based access for different users  
- Ensure secure authentication and authorization  
- Enable scalable microservices-based architecture  
- Improve transparency in billing and meter readings  

---

##  System Users & Roles

### 🔹 Admin
- Approves or rejects consumer registrations  
- Manages users, utilities, and tariff plans  

### 🔹 Billing Officer
- Reviews meter readings  
- Generates utility bills  

### 🔹 Accounts Officer
- Monitors payments and transaction records  

### 🔹 Consumer
- Views bills and payment history  
- Makes online payments  

---

##  System Architecture
- **Microservices-based architecture**
- **API Gateway** for request routing and security
- **Config Server** for centralized configuration
- **Eureka Server** for service discovery
- Independent services communicating via REST APIs

---

##  Security Implementation
- **JWT-based authentication** for secure access  
- **Role-based authorization** for all APIs  
- **Secure API Gateway** to validate tokens before routing requests  

---

##  Technologies Used

### Frontend
- Angular
- HTML, CSS, TypeScript

### Backend
- Java 17
- Spring Boot
- Spring WebFlux
- Spring Security
- Spring Cloud (Gateway, Config Server, Eureka)

### Database
- MongoDB

### Tools & Platforms
- Git & GitHub
- Postman
- Maven
- STS / VS Code

---

## Security Implementation
<img width="1370" height="459" alt="image" src="https://github.com/user-attachments/assets/383e28b0-48b3-400f-b983-ba95ffce8958" />

---

##  Application Flow
1. User registers in the system  
2. Admin approves or rejects the registration  
3. Approved user receives activation link via email  
4. User sets password and logs in  
5. Billing officer manages meter readings  
6. Bills are generated based on readings  
7. Consumer views and pays bills

### Config Repo: [Repo-link-config]([url](https://github.com/Poojithamodala/Utility-Billing-Config)) 

---
## ER Diagram
<img width="240" height="1125" alt="image" src="https://github.com/user-attachments/assets/df8d1aa7-2e13-47de-ab73-8393a9145b0f" />

### Database Design
## Users Collection
Stores login and authentication details for all system users.

Key Fields:
username, email, password
role (ADMIN, BILLING_OFFICER, ACCOUNTS_OFFICER, CONSUMER)
enabled – account activation status
failedAttempts – security & account lock feature
--> Used by Auth Service

## Consumers Collection
Stores consumer profile information.

Key Fields:
name, email, phone, address
username (linked with Auth Service)
createdAt
--> Used by Consumer Service

## Connections Collection
Represents an active utility connection for a consumer.

Key Fields:
consumerId
utilityType (Electricity, Water, etc.)
meterNumber
tariffPlanId
billingCycle
status (ACTIVE / INACTIVE)
--> Used by Connection Service

## Tariff Plans Collection
Stores tariff rules for billing calculation.

Key Fields:
utilityType
fixedCharge
taxPercentage
slabs (slab-based billing)
--> Used by Connection & Billing Services

## Meter Readings Collection
Stores meter readings recorded by billing officers.

Key Fields:
connectionId
previousReading, currentReading
unitsConsumed
readingDate
status (RECORDED / BILLED)
--> Used by Meter Reading Service

## Bills Collection
Stores generated bills based on meter readings.

Key Fields:
consumerId, connectionId
unitsConsumed
energyCharge, fixedCharge, taxAmount
totalAmount, outstandingAmount
status (PAID / DUE / OVERDUE)
--> Used by Billing Service

## Payments Collection
Stores payment transaction details.

Key Fields:
billId
amountPaid
paymentMode (UPI, CARD, etc.)
paymentStatus
referenceNumber
--> Used by Payment Service

