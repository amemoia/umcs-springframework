# Car Rent - Spring Integration

## Run (Spring Boot)

Set `DB` (and optionally `DB_USER`, `DB_PASS`) for the Hibernate connection. Example:

```zsh
export DB='jdbc:postgresql://HOST/DB?user=USER&password=PASS&sslmode=require'
```

Start the API:

```zsh
mvn spring-boot:run
```

## Endpoints

### Rentals
- `GET /rentals`
- `GET /rentals/users/{userId}`
- `POST /rentals/users/{userId}/rent/{vehicleId}`
- `POST /rentals/users/{userId}/return`

### Users
- `GET /users`
- `GET /users/{id}`

### Vehicles
- `GET /vehicles?available=false|true`
- `GET /vehicles/{id}`
- `POST /vehicles`
- `DELETE /vehicles/{id}`

### Categories
- `GET /categories`
- `GET /categories/{category}`

## Notes
- The CLI is still available via `com.umcsuser.carrent.Main`.
- For `POST /vehicles`, send JSON with a `category` field (`CAR` or `MOTORCYCLE`).
