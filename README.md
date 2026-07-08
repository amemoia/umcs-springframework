## Endpoints

### Authentication
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/users` - admin only


### Books
- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books` - admin only
- `PUT /api/books/{id}` - admin only
- `DELETE /api/books/{id}` - admin only

### Cart
- `GET /api/cart` - current user's cart
- `POST /api/cart` - current user
- `DELETE /api/cart` - current user
- `GET /api/cart/{login}` - admin view specific cart
- `POST /api/cart/{login}` - admin modify specific cart
- `DELETE /api/cart/{login}` - admin modify specific cart

### Orders
- `GET /api/orders` - admin only
- `GET /api/orders/my`
- `POST /api/orders/checkout`
- `PATCH /api/orders/{id}/status` - admin only

### Payments
- `POST /api/payments/{orderId}` - stripe checkout test mode
- `GET /api/payments/{orderId}` - inspect stripe session
- `POST /api/payments/webhook` - stripe webhook

### Stripe tests
- `STRIPE_SECRET_KEY` - stripe test secret key
- `STRIPE_WEBHOOK_SECRET` - webhook endpoint secret
- `STRIPE_SUCCESS_URL` - optional success redirect
- `STRIPE_CANCEL_URL` - optional cancel redirect
- `STRIPE_CURRENCY` - defaults to `pln`

### CLI mode
Set `APP_PROFILE=cli`

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

- `Chungus Adventure 1`
- `Legend of Solid Chungus`
