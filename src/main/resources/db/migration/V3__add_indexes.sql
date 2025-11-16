-- V3__add_indexes.sql
-- Performance indexes and additional constraints

-- ============================================
-- ENABLE EXTENSIONS (MUST BE FIRST!)
-- ============================================

CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ============================================
-- INDEXES for frequently queried columns
-- ============================================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_client_id ON users(client_id);
CREATE INDEX idx_users_enabled ON users(enabled) WHERE enabled = true;

CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_phone ON clients(phone);

CREATE INDEX idx_tables_available ON restaurant_tables(is_available) WHERE is_available = true;
CREATE INDEX idx_tables_capacity ON restaurant_tables(capacity);

CREATE INDEX idx_reservations_client_id ON reservations(client_id);
CREATE INDEX idx_reservations_table_id ON reservations(table_id);
CREATE INDEX idx_reservations_time ON reservations(reservation_time);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_reservations_time_status ON reservations(reservation_time, status) WHERE status = 'ACTIVE'::reservation_status;

CREATE INDEX idx_orders_client_id ON orders(client_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_time ON orders(order_time);
CREATE INDEX idx_orders_created_by ON orders(created_by_user_id);
CREATE INDEX idx_orders_client_status ON orders(client_id, status);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_dish_id ON order_items(dish_id);

CREATE INDEX idx_dishes_category ON dishes(category);
CREATE INDEX idx_dishes_available ON dishes(is_available) WHERE is_available = true;
CREATE INDEX idx_dishes_name ON dishes(name);
CREATE INDEX idx_dishes_name_trgm ON dishes USING gin(name gin_trgm_ops);

CREATE INDEX idx_dish_ingredients_dish ON dish_ingredients(dish_id);
CREATE INDEX idx_dish_ingredients_ingredient ON dish_ingredients(ingredient_id);

CREATE INDEX idx_ingredients_name ON ingredients(name);
CREATE INDEX idx_ingredients_low_stock ON ingredients(stock_quantity, min_stock_level) WHERE stock_quantity <= min_stock_level;

CREATE INDEX idx_sales_sale_time ON sales(sale_time);
CREATE INDEX idx_sales_payment_method ON sales(payment_method);
CREATE INDEX idx_sales_processed_by ON sales(processed_by_user_id);

CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_suppliers_inn ON suppliers(inn);

CREATE INDEX idx_supplies_supplier ON supplies(supplier_id);
CREATE INDEX idx_supplies_status ON supplies(status);
CREATE INDEX idx_supplies_supply_time ON supplies(supply_time);
CREATE INDEX idx_supplies_received_by ON supplies(received_by_user_id);

CREATE INDEX idx_supply_items_supply ON supply_items(supply_id);
CREATE INDEX idx_supply_items_ingredient ON supply_items(ingredient_id);

-- ============================================
-- ADDITIONAL CONSTRAINTS
-- ============================================

ALTER TABLE reservations ADD CONSTRAINT chk_reservation_future CHECK (reservation_time > created_at);
ALTER TABLE dishes ADD CONSTRAINT chk_dish_price_range CHECK (price BETWEEN 0.01 AND 999999.99);
ALTER TABLE ingredients ADD CONSTRAINT chk_ingredient_stock_positive CHECK (stock_quantity >= 0);

COMMENT ON COLUMN orders.total IS 'Auto-calculated sum of order_items. Updated by trigger fn_update_order_total';

-- ============================================
-- UNIQUE CONSTRAINTS
-- ============================================

CREATE UNIQUE INDEX idx_unique_table_reservation ON reservations(table_id, reservation_time) WHERE status = 'ACTIVE'::reservation_status;

-- ============================================
-- PERFORMANCE NOTES
-- ============================================

COMMENT ON INDEX idx_reservations_time_status IS 'Speeds up queries for active reservations in time range - FR040';
COMMENT ON INDEX idx_dishes_available IS 'Partial index for available dishes only - improves menu queries';
COMMENT ON INDEX idx_ingredients_low_stock IS 'Partial index for low stock alerts - manager dashboard';
COMMENT ON INDEX idx_dishes_name_trgm IS 'Trigram index for fuzzy search on dish names - FR230';

-- End of V3