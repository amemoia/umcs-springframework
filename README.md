## Endpoints

### Authentication
- `POST /api/auth/login`
- `POST /api/auth/register`

### Books
- `GET /api/books` - public browsing
- `GET /api/books/{id}` - public browsing
- `POST /api/books` - admin only
- `PUT /api/books/{id}` - admin only
- `DELETE /api/books/{id}` - admin only

### Cart
- `GET /api/cart` - user/admin
- `POST /api/cart` - add a book to cart
- `DELETE /api/cart` - remove a book from cart

### Orders
- `GET /api/orders` - admin only
- `GET /api/orders/my`
- `POST /api/orders/checkout`
- `PATCH /api/orders/{id}/status` - admin only

### Payments
- `POST /api/payments/{orderId}` - Stripe-style payment session stub

### Legacy endpoints
- `GET /api/users` - admin only
- `GET /api/rentals` - admin only
- `GET /api/categories`
- `GET /api/categories/{category}`

## Profiles
Default profile is `jpa`. Set `APP_PROFILE` to switch:

- `APP_PROFILE=json` uses JSON files from `carrent.json.*`
- `APP_PROFILE=jdbc` uses JDBC with `DB` (+ optional `DB_USER`, `DB_PASS`)
- `APP_PROFILE=jpa` uses Spring Data JPA with `DB` (+ optional `DB_USER`, `DB_PASS`)

Example:

```zsh
export APP_PROFILE=jpa
export DB='jdbc:postgresql://<host>/<db>?user=<user>&password=<pass>&sslmode=require'
mvn spring-boot:run
```

Default demo users:

- `admin` / `admin123`
- `user` / `user123`

Default demo books:

- `Clean Code`
- `Effective Java`