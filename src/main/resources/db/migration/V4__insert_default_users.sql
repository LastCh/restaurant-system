-- V4: Insert default users for testing
-- Password for all: admin123
-- IMPORTANT: Change passwords in production!

INSERT INTO users (username, password, full_name, phone, role, enabled, created_at, updated_at)
VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', '+79999999999', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Restaurant Manager', '+79999999998', 'MANAGER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('waiter', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Waiter', '+79999999997', 'WAITER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Add test clients
INSERT INTO clients (full_name, phone, email, created_at, updated_at)
VALUES
    ('Иван Иванов', '+79001234567', 'ivan@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Мария Петрова', '+79007654321', 'maria@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Add test dishes
INSERT INTO dishes (name, description, category, price, is_available, preparation_time_minutes, created_at, updated_at)
VALUES
    ('Борщ', 'Традиционный украинский борщ со сметаной', 'Супы', 350.00, true, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Стейк из говядины', 'Сочный стейк средней прожарки', 'Горячие блюда', 1200.00, true, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Цезарь с курицей', 'Классический салат Цезарь', 'Салаты', 450.00, true, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Тирамису', 'Итальянский десерт', 'Десерты', 380.00, true, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Капучино', 'Кофе с молочной пенкой', 'Напитки', 180.00, true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Add test tables
INSERT INTO restaurant_tables (table_number, capacity, is_available, location, created_at, updated_at)
VALUES
    ('1', 2, true, 'Main Hall', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('2', 4, true, 'Main Hall', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('3', 6, true, 'Main Hall', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('VIP-1', 8, true, 'VIP Room', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (table_number) DO NOTHING;
