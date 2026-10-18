package shop.model;

import java.math.BigDecimal;
import java.util.*;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    public Cart add(CartItem item) {
        items.add(item);
        return this;
    }

    public List<CartItem> items() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public BigDecimal total() {
        return items.stream().map(CartItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

   
    public Map<String, Integer> quantitiesBySku() {
        Map<String, Integer> result = new LinkedHashMap<>();
        items.forEach(i -> result.merge(i.sku(), i.quantity(), Integer::sum));
        return result;
    }
}
