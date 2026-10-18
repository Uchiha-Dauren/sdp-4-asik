package shop.model;

import java.math.BigDecimal;
import java.util.Map;

public class Order {
    private final String id;
    private final Customer customer;
    private final Map<String, Integer> quantities;
    private final BigDecimal total;
    private final String paymentId;
    private final String trackingNumber;
    private OrderStatus status = OrderStatus.PLACED;

    public Order(String id, Customer customer, Map<String, Integer> quantities,
                 BigDecimal total, String paymentId, String trackingNumber) {
        this.id = id;
        this.customer = customer;
        this.quantities = Map.copyOf(quantities);
        this.total = total;
        this.paymentId = paymentId;
        this.trackingNumber = trackingNumber;
    }

    public String id() { return id; }
    public Customer customer() { return customer; }
    public Map<String, Integer> quantities() { return quantities; }
    public BigDecimal total() { return total; }
    public String paymentId() { return paymentId; }
    public String trackingNumber() { return trackingNumber; }
    public OrderStatus status() { return status; }

    public void markCancelled() { this.status = OrderStatus.CANCELLED; }

    @Override
    public String toString() {
        return "Order{" + id + ", " + status + ", total=" + total + ", tracking=" + trackingNumber + "}";
    }
}
