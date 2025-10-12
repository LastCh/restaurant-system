-- V1__create_tables.sql
-- Initial schema for restaurant system (Postgres)

-- ENUM types
CREATE TYPE order_status AS ENUM ('pending','in_progress','completed','cancelled');
CREATE TYPE reservation_status AS ENUM ('active','cancelled','expired');
CREATE TYPE supply_status AS ENUM ('pending','confirmed','cancelled');
CREATE TYPE payment_method AS ENUM ('cash','card','online','other');

-- Clients (customers)
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Reservations (table bookings)
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    reservation_time TIMESTAMP WITH TIME ZONE NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 90,
    party_size INT NOT NULL DEFAULT 1 CHECK (party_size > 0),
    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    status reservation_status NOT NULL DEFAULT 'active',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
    total NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    status order_status NOT NULL DEFAULT 'pending',
    client_id BIGINT REFERENCES clients(id) ON DELETE SET NULL,
    reservation_id BIGINT REFERENCES reservations(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Dishes (menu items)
CREATE TABLE dishes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    is_available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Ingredients
-- cost_per_unit: cost price for accounting (per unit defined by unit column)
CREATE TABLE ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    unit VARCHAR(50) NOT NULL,                 -- e.g. kg, g, piece
    stock_quantity NUMERIC(12,3) NOT NULL DEFAULT 0.000, -- stored in the same units as 'unit'
    cost_per_unit NUMERIC(12,4) DEFAULT 0.0000, -- supplier cost per unit (for cost calculation)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Dish ingredients (recipe)
-- quantity = how many units of ingredient per single dish (in same unit as ingredient.unit)
CREATE TABLE dish_ingredients (
    id BIGSERIAL PRIMARY KEY,
    dish_id BIGINT NOT NULL REFERENCES dishes(id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    quantity NUMERIC(12,4) NOT NULL CHECK (quantity > 0),
    unit VARCHAR(50),  -- redundancy: unit at time of recipe (should match ingredients.unit)
    UNIQUE (dish_id, ingredient_id)
);

-- Orders items (which dishes and quantities in an order)
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    dish_id BIGINT NOT NULL REFERENCES dishes(id) ON DELETE RESTRICT,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0), -- snapshot of dish price at order time
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Sales (payment records). One sale per completed order (enforced by unique constraint on order_id)
CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    sale_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
    total NUMERIC(12,2) NOT NULL,
    payment_method payment_method,
    order_id BIGINT UNIQUE NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    receipt_number VARCHAR(100)
);

-- Suppliers
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    inn VARCHAR(100),
    phone VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Supplies (deliveries)
CREATE TABLE supplies (
    id BIGSERIAL PRIMARY KEY,
    supply_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
    supplier_id BIGINT REFERENCES suppliers(id) ON DELETE SET NULL,
    status supply_status NOT NULL DEFAULT 'pending',
    total_cost NUMERIC(14,2) DEFAULT 0.00, -- calculated from supply_items or set on confirm
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Supply items (which ingredients and how much in a supply)
CREATE TABLE supply_items (
    id BIGSERIAL PRIMARY KEY,
    supply_id BIGINT NOT NULL REFERENCES supplies(id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    quantity NUMERIC(12,4) NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,4) DEFAULT 0.0000, -- supplier price per unit for this supply item
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Audit trigger helpers (optional): we'll add triggers/functions in V2

-- End of V1
