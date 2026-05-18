CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    login VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    rented_vehicle_id UUID
);

CREATE TABLE IF NOT EXISTS vehicles (
    id UUID PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    price FLOAT NOT NULL,
    rented BOOLEAN NOT NULL,
    category VARCHAR(50) NOT NULL,
    plate VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS rentals (
    id UUID PRIMARY KEY,
    vehicle_id UUID NOT NULL,
    user_id UUID NOT NULL,
    rent_date_time TIMESTAMP NOT NULL,
    return_date_time TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
