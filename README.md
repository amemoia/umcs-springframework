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