package shop.model;

import java.math.BigDecimal;


public record CartItem(String sku, String name, int quantity, BigDecimal unitPrice) {

    public CartItem {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU must not be blank");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice.signum() < 0) throw new IllegalArgumentException("Price must not be negative");
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
