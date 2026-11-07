# Mini E-Commerce Platform & Order Management Engine

[![Java](https://img.shields.io/badge/Java-17-orange.svg?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-blue.svg?logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![H2 Database](https://img.shields.io/badge/H2-In--Memory%20DB-darkblue.svg)](https://www.h2database.com/)
[![Frontend](https://img.shields.io/badge/Frontend-Vanilla%20ES6%20%2B%20CSS3-yellow.svg?logo=javascript&logoColor=white)](./frontend/index.html)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

A full-stack e-commerce web application featuring dynamic catalog browsing, client-side cart orchestration, real-time stock-deduction logic, and a transactional order fulfillment engine backed by Spring Boot 3 and Hibernate/JPA.

---

## Architecture Overview

The system is built on a decoupled client-server architecture:
- **Backend Service:** Spring Boot REST API managing persistent catalog state, transactional cart checkout, and inventory integrity.
- **Data Persistence:** Spring Data JPA with an in-memory H2 database pre-seeded with categorized inventory.
- **Frontend Client:** Responsive single-page application built with modern vanilla JavaScript (ES6+ async/await fetch API) and CSS Grid.

```mermaid
graph LR
    subgraph Client ["Frontend (Browser)"]
        UI["Product Catalog & Filters"]
        Cart["Client-Side Cart Manager"]
        CheckoutForm["Checkout Modal"]
    end

    subgraph Backend ["Spring Boot 3 API"]
        PC["ProductController (/api/products)"]
        OC["OrderController (/api/orders)"]
        PR["ProductRepository (JPA)"]
        OR["OrderRepository (JPA)"]
    end

    subgraph Storage ["Database"]
        H2[("H2 In-Memory DB")]
    end

    UI -->|GET /api/products| PC
    Cart --> CheckoutForm
    CheckoutForm -->|POST /api/orders/checkout| OC
    PC --> PR
    OC --> PR
    OC --> OR
    PR --> H2
    OR --> H2
```

---

## Domain Model & Entity Relationship

The domain schema separates catalog items from order snapshots to ensure price immutability across historical transactions:

```mermaid
erDiagram
    PRODUCT ||--o{ ORDER_ITEM : "referenced in"
    ORDER ||--|{ ORDER_ITEM : "contains"

    PRODUCT {
        bigint id PK
        string name
        string description
        double price
        int stock
        string category
        string imageUrl
    }

    ORDER {
        bigint id PK
        string customerName
        string customerEmail
        string shippingAddress
        double totalAmount
        timestamp createdAt
        string status
    }

    ORDER_ITEM {
        bigint id PK
        bigint order_id FK
        bigint productId
        string productName
        double unitPrice
        int quantity
    }
```

### Key Engineering Features
- **Price Immutability:** `OrderItem` captures historical `unitPrice` and `productName` at the exact moment of order placement, preventing catalog price updates from altering historical invoices.
- **Inventory Concurrency Guard:** The checkout flow validates available stock per item before deduction. If requested volume exceeds available inventory, the transaction returns an HTTP `409 Conflict` with a descriptive message.
- **Cascade Persistence:** The `Order` entity owns the lifecycle of its `OrderItem` records via `CascadeType.ALL` and `orphanRemoval = true`.

---

## Order Lifecycle

```mermaid
stateDiagram-v2
    [*] --> PLACED: POST /api/orders/checkout
    PLACED --> SHIPPED: PATCH /api/orders/{id}/status
    SHIPPED --> DELIVERED: PATCH /api/orders/{id}/status
    DELIVERED --> [*]
```

---

## REST API Specification

### Products API

| Method | Endpoint | Description | Query Parameters / Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | Retrieve catalog items | Optional `?category={name}` |
| `GET` | `/api/products/{id}` | Get single product by ID | None |
| `POST` | `/api/products` | Create a new product | `Product` JSON body |
| `DELETE` | `/api/products/{id}` | Remove a product | None |

### Orders & Checkout API

| Method | Endpoint | Description | Payload / Response |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/orders` | List all historical orders | Returns array of orders with line items |
| `GET` | `/api/orders/{id}` | Retrieve order details by ID | Returns single order DTO |
| `POST` | `/api/orders/checkout` | Process checkout & deduct stock | Returns HTTP 201 Created on success |
| `PATCH` | `/api/orders/{id}/status` | Update fulfillment state | `{"status": "SHIPPED"}` |

#### Sample Checkout Request (`POST /api/orders/checkout`)
```json
{
  "customerName": "Jane Doe",
  "customerEmail": "jane.doe@example.co.uk",
  "shippingAddress": "10 Downing Street, London, SW1A 2AA",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

---

## Quickstart & Local Installation

### Prerequisites
- **JDK 17** or higher
- **Maven 3.8+** (or included Maven wrapper)
- Any modern web browser

### 1. Launch the Backend
```bash
# Clone the repository
git clone https://github.com/sricharanperala111-eng/Ecommerce_mini.git
cd Ecommerce_mini

# Build and start Spring Boot
mvn clean spring-boot:run
```
The REST API will bind to `http://localhost:8081`.

### 2. Launch the Storefront
Simply open `frontend/index.html` in your web browser:
```bash
# Windows
start frontend/index.html

# macOS
open frontend/index.html

# Linux
xdg-open frontend/index.html
```

### 3. Inspect In-Memory Database (H2 Console)
- **URL:** `http://localhost:8081/h2-console`
- **JDBC URL:** `jdbc:h2:mem:shopdb`
- **Username:** `sa`
- **Password:** *(empty)*

---

## License
This project is open-source and available under the [MIT License](LICENSE).
