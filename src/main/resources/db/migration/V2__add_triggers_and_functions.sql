-- V2__triggers_and_functions.sql
-- Functions and triggers to keep totals and stocks in sync.

-- 1) Update orders.total when order_items change
CREATE OR REPLACE FUNCTION fn_update_order_total(p_order_id BIGINT) RETURNS NUMERIC AS $$
DECLARE
    new_total NUMERIC(14,2);
BEGIN
    SELECT COALESCE(SUM(oi.unit_price * oi.quantity), 0.00) INTO new_total
    FROM order_items oi
    WHERE oi.order_id = p_order_id;

    UPDATE orders SET total = new_total, updated_at = now() WHERE id = p_order_id;
    RETURN new_total;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trg_order_items_changed() RETURNS TRIGGER AS $$
BEGIN
    -- call function to recalc total for relevant order
    PERFORM fn_update_order_total(COALESCE(NEW.order_id, OLD.order_id));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER order_items_after_ins_upd_del
AFTER INSERT OR UPDATE OR DELETE ON order_items
FOR EACH ROW EXECUTE FUNCTION trg_order_items_changed();

-- 2) On order status -> when set to 'completed' create sale (if not exists) AND deduct ingredients safely
CREATE OR REPLACE FUNCTION trg_orders_after_update() RETURNS TRIGGER AS $$
DECLARE
    rec RECORD;
    v_sale_exists BOOLEAN;
BEGIN
    IF NEW.status = 'completed' AND OLD.status IS DISTINCT FROM NEW.status THEN
        -- create sale only if doesn't exist
        SELECT EXISTS(SELECT 1 FROM sales WHERE order_id = NEW.id) INTO v_sale_exists;
        IF NOT v_sale_exists THEN
            INSERT INTO sales (sale_time, total, payment_method, order_id)
            VALUES (now(), NEW.total, 'other', NEW.id);
        END IF;

        -- deduct ingredients: do it in a safe way using SELECT ... FOR UPDATE to avoid races
        FOR rec IN
            SELECT di.ingredient_id,
                   SUM(oi.quantity * di.quantity) AS qty_to_deduct
            FROM order_items oi
            JOIN dish_ingredients di ON oi.dish_id = di.dish_id
            WHERE oi.order_id = NEW.id
            GROUP BY di.ingredient_id
        LOOP
            -- lock ingredient row
            PERFORM 1 FROM ingredients WHERE id = rec.ingredient_id FOR UPDATE;
            UPDATE ingredients
            SET stock_quantity = stock_quantity - rec.qty_to_deduct,
                updated_at = now()
            WHERE id = rec.ingredient_id;
            -- Note: negative stock is possible; you may want to enforce CHECK or raise if stock < 0
        END LOOP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER orders_after_update
AFTER UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION trg_orders_after_update();

-- 3) On supplies status -> when set to 'confirmed' add quantities to ingredient stock and calculate supply total
CREATE OR REPLACE FUNCTION trg_supplies_after_update() RETURNS TRIGGER AS $$
DECLARE
    rec RECORD;
    v_total NUMERIC(14,2) := 0;
BEGIN
    IF NEW.status = 'confirmed' AND OLD.status IS DISTINCT FROM NEW.status THEN
        FOR rec IN SELECT ingredient_id, quantity, unit_price FROM supply_items WHERE supply_id = NEW.id
        LOOP
            PERFORM 1 FROM ingredients WHERE id = rec.ingredient_id FOR UPDATE;
            UPDATE ingredients
            SET stock_quantity = stock_quantity + rec.quantity,
                updated_at = now()
            WHERE id = rec.ingredient_id;

            v_total := v_total + COALESCE(rec.quantity * rec.unit_price, 0);
        END LOOP;

        UPDATE supplies SET total_cost = v_total, updated_at = now() WHERE id = NEW.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER supplies_after_update
AFTER UPDATE ON supplies
FOR EACH ROW EXECUTE FUNCTION trg_supplies_after_update();

-- 4) Utility: recalc dish cost (sum of ingredients cost)
CREATE OR REPLACE FUNCTION fn_calculate_dish_cost(p_dish_id BIGINT) RETURNS NUMERIC AS $$
DECLARE
    total_cost NUMERIC := 0;
BEGIN
    SELECT COALESCE(SUM(di.quantity * i.cost_per_unit),0) INTO total_cost
    FROM dish_ingredients di
    JOIN ingredients i ON di.ingredient_id = i.id
    WHERE di.dish_id = p_dish_id;

    RETURN total_cost;
END;
$$ LANGUAGE plpgsql;

-- 5) Trigger to keep updated_at timestamps for some tables
CREATE OR REPLACE FUNCTION fn_update_timestamp() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach update timestamp to relevant tables
CREATE TRIGGER ts_clients BEFORE UPDATE ON clients FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();
CREATE TRIGGER ts_reservations BEFORE UPDATE ON reservations FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();
CREATE TRIGGER ts_orders BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();
CREATE TRIGGER ts_dishes BEFORE UPDATE ON dishes FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();
CREATE TRIGGER ts_ingredients BEFORE UPDATE ON ingredients FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();
CREATE TRIGGER ts_supplies BEFORE UPDATE ON supplies FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();
CREATE TRIGGER ts_suppliers BEFORE UPDATE ON suppliers FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

-- End of V2
