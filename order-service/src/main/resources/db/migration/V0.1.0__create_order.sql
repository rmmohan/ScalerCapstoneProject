CREATE TABLE IF NOT EXISTS `t_payment_status` (
   `id` INT NOT NULL AUTO_INCREMENT,
   `status` VARCHAR(45) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE INDEX `status_UNIQUE` (`status` ASC) VISIBLE)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `t_orders` (
   `id` INT NOT NULL AUTO_INCREMENT,
   `order_number` VARCHAR(50) NOT NULL,
   `first_name` VARCHAR(50) NOT NULL,
   `last_name` VARCHAR(50) NOT NULL,
   `email` VARCHAR(50) NOT NULL,
   `sku_code` VARCHAR(50) NULL DEFAULT NULL,
   `price` DECIMAL(19,2) NULL DEFAULT NULL,
   `quantity` INT NULL DEFAULT NULL,
   `payment_status_id` INT NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE INDEX `order_number_UNIQUE` (`order_number` ASC) VISIBLE,
   INDEX `fk_t_orders_payment_status_idx` (`payment_status_id` ASC) VISIBLE,
   CONSTRAINT `fk_t_orders_payment_status`
   FOREIGN KEY (`payment_status_id`)
   REFERENCES `t_payment_status` (`id`))
ENGINE = InnoDB;