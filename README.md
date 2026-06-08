## Endpoints

### Rentals
- `GET /api/rentals` - admin only
- `GET /api/rentals/my`
- `POST /api/rentals/rent`
- `POST /api/rentals/return`

### Users
- `GET /api/users` - admin only
- `GET /api/users/{id}` - admin only

### Vehicles
- `GET /api/vehicles?available=false|true`
- `GET /api/vehicles/{id}`
- `POST /api/vehicles`
- `DELETE /api/vehicles/{id}`

### Categories
- `GET /api/categories`
- `GET /api/categories/{category}`

## Profiles
Default profile is `json` (no DB required). Set `APP_PROFILE` to switch:

- `APP_PROFILE=json` uses JSON files from `carrent.json.*`
- `APP_PROFILE=jdbc` uses JDBC with `DB` (+ optional `DB_USER`, `DB_PASS`)
- `APP_PROFILE=jpa` uses Spring Data JPA with `DB` (+ optional `DB_USER`, `DB_PASS`)

Example:

```zsh
export APP_PROFILE=jpa
export DB='jdbc:postgresql://<host>/<db>?user=<user>&password=<pass>&sslmode=require'
mvn spring-boot:run
```
