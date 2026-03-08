# 👟 ShoeShop E-Commerce Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![React](https://img.shields.io/badge/React-18.x-blue)](https://reactjs.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Hệ thống thương mại điện tử chuyên kinh doanh giày dép với kiến trúc Full-stack hiện đại, hỗ trợ thanh toán, quản lý kho và triển khai tự động hóa.

---

## 🌐 Live Demo
* **Website:** [https://shoeshopecommerce.dpdns.org/](https://shoeshopecommerce.dpdns.org/)
* **Admin Dashboard:** `https://shoeshopecommerce.dpdns.org/admin`

### 🔑 Thông tin quản trị (Admin Credentials)
Sử dụng tài khoản dưới đây để kiểm thử các tính năng dành cho người quản trị:
* **Username:** `admin123`
* **Password:** `Admin123@`

---

## 🛠 Công nghệ sử dụng (Tech Stack)

### Backend
* **Core:** Java 21, Spring Boot 3.5.10.
* **Security:** Spring Security, JWT (Stateless), Google OAuth2.
* **Database:** MySQL 8.0.45 (Primary), Redis (Caching & Session).
* **API Documentation:** Swagger/OpenAPI UI.

### Frontend
* **Library:** ReactJS (Vite).
* **State Management:** Redux Toolkit / Context API.
* **Styling:** Tailwind CSS.

### DevOps & Infrastructure
* **CI/CD:** Jenkins, Ansible.
* **Containerization:** Docker, Docker Compose.
* **Orchestration:** Kubernetes (GKE) / Docker Swarm & ArgoCD (GitOps).
* **Server:** Nginx Reverse Proxy, HAProxy Load Balancer.

---

## 🏗 Kiến trúc hệ thống (Architecture)

Dự án áp dụng **Layered Architecture** để tách biệt mã nguồn:

```text
com.shoeshop
├── config/         # Security, Redis, CORS, Cloud Config
├── controller/     # REST API Endpoints
├── service/        # Business Logic & Validation
├── repository/     # Data Access Layer (Spring Data JPA)
├── entity/         # Database Models (Hibernate)
├── dto/            # Data Transfer Objects (Request/Response)
└── exception/      # Global Exception Handling
