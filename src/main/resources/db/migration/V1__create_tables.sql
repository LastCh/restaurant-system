-- V1__create_tables.sql
-- Initial schema for restaurant system (PostgreSQL)

-- ============================================
-- ENUM TYPES
-- ============================================

CREATE TYPE user_role AS ENUM ('ADMIN', 'MANAGER', 'WAITER', 'CLIENT');
CREATE TYPE order_status AS ENUM ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE reservation_status AS ENUM ('ACTIVE', 'CANCELLED', 'EXPIRED');
CREATE TYPE supply_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED');
CREATE TYPE payment_method AS ENUM ('CASH', 'CARD', 'ONLINE', 'OTHER');


-- ============================================
-- CORE TABLES
-- ============================================

-- Clients (customers who can register and make orders)
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Users (authentication: staff + clients who register)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- BCrypt hash
    role user_role NOT NULL DEFAULT 'CLIENT',
    enabled BOOLEAN NOT NULL DEFAULT true,

    -- Link to client if this user is a customer
    client_id BIGINT UNIQUE REFERENCES clients(id) ON DELETE SET NULL,

    -- Staff information (for non-client users: ADMIN, MANAGER, WAITER)
    full_name VARCHAR(255),
    phone VARCHAR(50),
    staff_code VARCHAR(50),  -- табельный номер

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now(),

    -- Constraint: either client_id is set (CLIENT role) or staff fields are set
    CONSTRAINT chk_user_type CHECK (
        (role = 'CLIENT' AND client_id IS NOT NULL) OR
        (role IN ('ADMIN', 'MANAGER', 'WAITER') AND client_id IS NULL)
    )
);

-- Restaurant tables (physical tables for reservations)
CREATE TABLE restaurant_tables (
    id BIGSERIAL PRIMARY KEY,
    table_number VARCHAR(20) UNIQUE NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0),
    is_available BOOLEAN NOT NULL DEFAULT true,
    location VARCHAR(100),  -- e.g., "Main hall", "Terrace", "VIP room"
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Reservations (table bookings)
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    reservation_time TIMESTAMP WITH TIME ZONE NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 90,
    party_size INT NOT NULL DEFAULT 1 CHECK (party_size > 0),

    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,  -- ⚠️ Changed from CASCADE
    table_id BIGINT REFERENCES restaurant_tables(id) ON DELETE SET NULL,

    status reservation_status NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,  -- Special requests from client

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Dishes (menu items)
CREATE TABLE dishes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,  -- ✅ Added: description for menu
    category VARCHAR(100),
    price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    is_available BOOLEAN NOT NULL DEFAULT true,
    image_url VARCHAR(500),  -- ✅ Added: for UI
    preparation_time_minutes INT DEFAULT 15,  -- ✅ Added: for kitchen planning

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Ingredients (stock management)
CREATE TABLE ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    unit VARCHAR(50) NOT NULL,  -- kg, g, l, ml, piece
    stock_quantity NUMERIC(12,3) NOT NULL DEFAULT 0.000,
    cost_per_unit NUMERIC(12,4) DEFAULT 0.0000,
    min_stock_level NUMERIC(12,3) DEFAULT 0.000,  -- ✅ Added: для уведомлений о низких остатках

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Dish ingredients (recipes)
CREATE TABLE dish_ingredients (
    id BIGSERIAL PRIMARY KEY,
    dish_id BIGINT NOT NULL REFERENCES dishes(id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    quantity NUMERIC(12,4) NOT NULL CHECK (quantity > 0),
    unit VARCHAR(50),

    UNIQUE (dish_id, ingredient_id)
);

-- Orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
    total NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    status order_status NOT NULL DEFAULT 'PENDING',

    client_id BIGINT REFERENCES clients(id) ON DELETE SET NULL,
    reservation_id BIGINT REFERENCES reservations(id) ON DELETE SET NULL,

    notes TEXT,  -- ✅ Added: special instructions
    created_by_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,  -- ✅ Added: who created order

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Order items (which dishes in an order)
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    dish_id BIGINT NOT NULL REFERENCES dishes(id) ON DELETE RESTRICT,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Sales (payment records)
CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    sale_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
    total NUMERIC(12,2) NOT NULL,
    payment_method payment_method,
    order_id BIGINT UNIQUE NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    receipt_number VARCHAR(100),

    processed_by_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL  -- ✅ Added: кто оформил оплату
);

-- Suppliers
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    inn VARCHAR(100),
    phone VARCHAR(50),
    email VARCHAR(255),  -- ✅ Added
    address TEXT,  -- ✅ Added
    contact_person VARCHAR(255),  -- ✅ Added

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Supplies (deliveries)
CREATE TABLE supplies (
    id BIGSERIAL PRIMARY KEY,
    supply_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
    supplier_id BIGINT REFERENCES suppliers(id) ON DELETE SET NULL,
    status supply_status NOT NULL DEFAULT 'PENDING',
    total_cost NUMERIC(14,2) DEFAULT 0.00,

    notes TEXT,  -- ✅ Added
    received_by_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,  -- ✅ Added

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Supply items
CREATE TABLE supply_items (
    id BIGSERIAL PRIMARY KEY,
    supply_id BIGINT NOT NULL REFERENCES supplies(id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    quantity NUMERIC(12,4) NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,4) DEFAULT 0.0000,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- ============================================
-- COMMENTS (for documentation)
-- ============================================

COMMENT ON TABLE users IS 'Пользователи системы: персонал (ADMIN, MANAGER, WAITER) и клиенты (CLIENT)';
COMMENT ON TABLE clients IS 'Клиенты ресторана (могут регистрироваться как users)';
COMMENT ON TABLE restaurant_tables IS 'Физические столы в ресторане для бронирования';
COMMENT ON TABLE reservations IS 'Бронирования столов';
COMMENT ON TABLE dishes IS 'Меню ресторана';
COMMENT ON TABLE ingredients IS 'Склад ингредиентов';
COMMENT ON TABLE dish_ingredients IS 'Рецепты блюд (состав)';
COMMENT ON TABLE orders IS 'Заказы клиентов';
COMMENT ON TABLE order_items IS 'Позиции в заказе';
COMMENT ON TABLE sales IS 'Оплаты (генерируются автоматически при завершении заказа)';
COMMENT ON TABLE suppliers IS 'Поставщики продуктов';
COMMENT ON TABLE supplies IS 'Поставки от поставщиков';
COMMENT ON TABLE supply_items IS 'Позиции в поставке';

-- End of V1
