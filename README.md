# Mini E-commerce Platform

A product catalog with cart, checkout, and stock management.

**Stack:** Java 17, Spring Boot 3, Spring Data JPA, H2 in-memory database, vanilla JS frontend.

## Features
- Product catalog with category filters
- Client-side cart (add/remove items, live totals)
- Checkout flow that creates an order, deducts stock, and validates availability
- Order history stored server-side with line items
- Seeded with 8 sample products on startup

## How to run

### 1. Start the backend
```bash
cd ecommerce-mini
mvn spring-boot:run
```
Backend runs on **http://localhost:8081**
H2 console: http://localhost:8081/h2-console (JDBC URL: `jdbc:h2:mem:shopdb`, user: `sa`, no password)

### 2. Open the frontend
Open `frontend/index.html` directly in your browser. It calls the backend at `localhost:8081`.

## API endpoints
| Method | Endpoint                     | Description                          |
|--------|-------------------------------|---------------------------------------|
| GET    | /api/products                  | List products (optional `?category=`) |
| GET    | /api/products/{id}              | Get one product                        |
| POST   | /api/products                  | Add a product                          |
| DELETE | /api/products/{id}              | Delete a product                       |
| GET    | /api/orders                    | List all orders                        |
| GET    | /api/orders/{id}                | Get one order                          |
| POST   | /api/orders/checkout            | Place an order from cart items         |
| PATCH  | /api/orders/{id}/status          | Update order status                     |

## Resume bullet point ideas
- Built a full-stack e-commerce app with Spring Boot, handling product catalog management, cart logic, and a transactional checkout flow that validates and deducts inventory.
- Modeled a relational schema (Product, Order, OrderItem) with JPA one-to-many relationships and exposed a REST API for a JS storefront.

## Ideas to extend further (good for interview talking points)
- Integrate Stripe test-mode payments at checkout
- Add Spring Security with role-based access (admin vs. customer)
- Add pagination and search to the product catalog
- Move to PostgreSQL and add Flyway migrations
- Add order status tracking UI (Placed → Shipped → Delivered)
