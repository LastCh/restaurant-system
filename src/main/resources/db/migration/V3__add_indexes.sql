-- V3__indexes_and_constraints.sql

-- Indexes for performance
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_reservations_client_id ON reservations(client_id);
CREATE INDEX idx_reservations_time ON reservations(reservation_time);
CREATE INDEX idx_orders_client_id ON orders(client_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_dish_id ON order_items(dish_id);
CREATE INDEX idx_dish_ingredients_dish ON dish_ingredients(dish_id);
CREATE INDEX idx_dish_ingredients_ingredient ON dish_ingredients(ingredient_id);
CREATE INDEX idx_supplies_supplier ON supplies(supplier_id);
CREATE INDEX idx_supply_items_supply ON supply_items(supply_id);
CREATE INDEX idx_ingredients_name ON ingredients(name);

-- Optional: ensure order cannot be completed twice to create duplicate sale
-- (we already have unique constraint on sales.order_id)

-- End of V3
