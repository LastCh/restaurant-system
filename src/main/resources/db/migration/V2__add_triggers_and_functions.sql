-- V2__triggers_and_functions.sql
-- Functions and triggers to keep totals and stocks in sync.

-- ============================================
-- 1) AUTO-UPDATE orders.total when order_items change
-- ============================================

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
    PERFORM fn_update_order_total(COALESCE(NEW.order_id, OLD.order_id));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER order_items_after_ins_upd_del
AFTER INSERT OR UPDATE OR DELETE ON order_items
FOR EACH ROW EXECUTE FUNCTION trg_order_items_changed();

-- ============================================
-- 2) AUTO-CREATE sale and DEDUCT ingredients when order completed
-- ============================================

CREATE OR REPLACE FUNCTION trg_orders_after_update() RETURNS TRIGGER AS $$
DECLARE
    rec RECORD;
    v_sale_exists BOOLEAN;
    v_current_stock NUMERIC;
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status IS DISTINCT FROM NEW.status THEN

        -- A) Create sale record if not exists
        SELECT EXISTS(SELECT 1 FROM sales WHERE order_id = NEW.id) INTO v_sale_exists;
        IF NOT v_sale_exists THEN
            INSERT INTO sales (sale_time, total, payment_method, order_id, processed_by_user_id)
            VALUES (now(), NEW.total, 'OTHER', NEW.id, NEW.created_by_user_id);
        END IF;

        -- B) Deduct ingredients from stock (with safety check)
        FOR rec IN
            SELECT di.ingredient_id,
                   SUM(oi.quantity * di.quantity) AS qty_to_deduct
            FROM order_items oi
            JOIN dish_ingredients di ON oi.dish_id = di.dish_id
            WHERE oi.order_id = NEW.id
            GROUP BY di.ingredient_id
        LOOP
            -- Lock ingredient row to prevent race conditions
            SELECT stock_quantity INTO v_current_stock
            FROM ingredients WHERE id = rec.ingredient_id FOR UPDATE;

            -- ⚠️ SAFETY CHECK: prevent negative stock
            IF v_current_stock < rec.qty_to_deduct THEN
                RAISE EXCEPTION 'Insufficient stock for ingredient_id=%: available=%, required=%',
                    rec.ingredient_id, v_current_stock, rec.qty_to_deduct;
            END IF;

            -- Deduct stock
            UPDATE ingredients
            SET stock_quantity = stock_quantity - rec.qty_to_deduct,
                updated_at = now()
            WHERE id = rec.ingredient_id;
        END LOOP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER orders_after_update
AFTER UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION trg_orders_after_update();

-- ============================================
-- 3) AUTO-ADD stock and CALCULATE total when supply confirmed
-- ============================================

CREATE OR REPLACE FUNCTION trg_supplies_after_update() RETURNS TRIGGER AS $$
DECLARE
    rec RECORD;
    v_total NUMERIC(14,2) := 0;
BEGIN
    IF NEW.status = 'CONFIRMED' AND OLD.status IS DISTINCT FROM NEW.status THEN

        FOR rec IN
            SELECT ingredient_id, quantity, unit_price
            FROM supply_items
            WHERE supply_id = NEW.id
        LOOP
            -- Lock and update ingredient stock
            PERFORM 1 FROM ingredients WHERE id = rec.ingredient_id FOR UPDATE;

            UPDATE ingredients
            SET stock_quantity = stock_quantity + rec.quantity,
                updated_at = now()
            WHERE id = rec.ingredient_id;

            -- Calculate total cost
            v_total := v_total + COALESCE(rec.quantity * rec.unit_price, 0);
        END LOOP;

        -- Update supply total_cost
        UPDATE supplies
        SET total_cost = v_total, updated_at = now()
        WHERE id = NEW.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER supplies_after_update
AFTER UPDATE ON supplies
FOR EACH ROW EXECUTE FUNCTION trg_supplies_after_update();

-- ============================================
-- 4) UTILITY: Calculate dish cost from ingredients
-- ============================================

CREATE OR REPLACE FUNCTION fn_calculate_dish_cost(p_dish_id BIGINT) RETURNS NUMERIC AS $$
DECLARE
    total_cost NUMERIC := 0;
BEGIN
    SELECT COALESCE(SUM(di.quantity * i.cost_per_unit), 0) INTO total_cost
    FROM dish_ingredients di
    JOIN ingredients i ON di.ingredient_id = i.id
    WHERE di.dish_id = p_dish_id;

    RETURN total_cost;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 5) AUTO-UPDATE updated_at timestamps
-- ============================================

CREATE OR REPLACE FUNCTION fn_update_timestamp() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach to tables with updated_at column
CREATE TRIGGER ts_clients BEFORE UPDATE ON clients
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

CREATE TRIGGER ts_reservations BEFORE UPDATE ON reservations
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

CREATE TRIGGER ts_orders BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

CREATE TRIGGER ts_dishes BEFORE UPDATE ON dishes
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

CREATE TRIGGER ts_ingredients BEFORE UPDATE ON ingredients
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

CREATE TRIGGER ts_supplies BEFORE UPDATE ON supplies
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

CREATE TRIGGER ts_suppliers BEFORE UPDATE ON suppliers
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

CREATE TRIGGER ts_users BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

CREATE TRIGGER ts_restaurant_tables BEFORE UPDATE ON restaurant_tables
    FOR EACH ROW EXECUTE FUNCTION fn_update_timestamp();

-- ============================================
-- 6) CHECK low stock levels (notification helper)
-- ============================================

CREATE OR REPLACE FUNCTION fn_get_low_stock_ingredients()
RETURNS TABLE(ingredient_id BIGINT, ingredient_name VARCHAR, current_stock NUMERIC, min_level NUMERIC) AS $$
BEGIN
    RETURN QUERY
    SELECT id, name, stock_quantity, min_stock_level
    FROM ingredients
    WHERE stock_quantity <= min_stock_level
    ORDER BY stock_quantity ASC;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION fn_get_low_stock_ingredients() IS
'Returns list of ingredients with stock below minimum level - for notifications';

-- ============================================
-- 7) CHECK ingredient availability for dish
-- ============================================

CREATE OR REPLACE FUNCTION fn_check_dish_available(p_dish_id BIGINT, p_quantity INT DEFAULT 1)
RETURNS BOOLEAN AS $$
DECLARE
    rec RECORD;
    v_required_qty NUMERIC;
BEGIN
    FOR rec IN
        SELECT di.ingredient_id, di.quantity, i.stock_quantity
        FROM dish_ingredients di
        JOIN ingredients i ON di.ingredient_id = i.id
        WHERE di.dish_id = p_dish_id
    LOOP
        v_required_qty := rec.quantity * p_quantity;

        IF rec.stock_quantity < v_required_qty THEN
            RETURN FALSE;  -- Insufficient stock
        END IF;
    END LOOP;

    RETURN TRUE;  -- All ingredients available
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION fn_check_dish_available(BIGINT, INT) IS
'Check if dish can be prepared (enough ingredients in stock). FR060';

-- End of V2
