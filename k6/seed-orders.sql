SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE payments;
TRUNCATE TABLE order_item;
TRUNCATE TABLE orders;
SET FOREIGN_KEY_CHECKS = 1;

SET @N = 100000;
SET @user_id = 1;

DROP PROCEDURE IF EXISTS seed_orders;
DELIMITER //
CREATE PROCEDURE seed_orders()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= @N DO
      INSERT INTO orders
        (user_id, status, ordered_at, coupon_id, discount_amount, final_amount, created_at, updated_at)
      VALUES
        (@user_id, 'PENDING', NOW(), NULL, 0, 10000, NOW(), NOW());

INSERT INTO order_item
    (order_id, product_id, product_name, unit_price, quantity, created_at, updated_at)
VALUES
    (LAST_INSERT_ID(), 1, '부하테스트상품', 10000, 1, NOW(), NOW());

SET i = i + 1;
END WHILE;
END //
  DELIMITER ;

CALL seed_orders();
DROP PROCEDURE seed_orders;

SELECT MIN(id) AS order_start, MAX(id) AS order_end, COUNT(*) FROM orders;