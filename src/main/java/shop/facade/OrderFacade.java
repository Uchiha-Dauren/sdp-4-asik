package shop.facade;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import shop.model.Cart;
import shop.model.Customer;
import shop.model.Order;
import shop.model.OrderStatus;
import shop.subsystem.*;


public class OrderFacade {
    private final InventoryService inventory;
    private final PaymentProcessor payments;
    private final ShippingService shipping;
    private final NotificationService notifications;
    private final Map<String, Order> orders = new HashMap<>();
    private int nextOrderNumber = 1001;

    public OrderFacade(InventoryService inventory, PaymentProcessor payments,
                       ShippingService shipping, NotificationService notifications) {
        this.inventory = inventory;
        this.payments = payments;
        this.shipping = shipping;
        this.notifications = notifications;
    }

    public Order placeOrder(Cart cart, Customer customer) {
        requireValid(cart, customer);
        Map<String, Integer> quantities = cart.quantitiesBySku();
        Deque<Runnable> undoActions = new ArrayDeque<>(); // compensation for every completed step

        try {
            String reservationId = inventory.reserve(quantities);
            undoActions.push(() -> inventory.release(reservationId));

            String paymentId = payments.charge(customer.id(), cart.total());
            undoActions.push(() -> payments.refund(paymentId));

            String trackingNumber = shipping.schedule(customer.address(), quantities);
            undoActions.push(() -> shipping.cancel(trackingNumber));

            inventory.commit(reservationId);

            Order order = new Order("ORD-" + nextOrderNumber++, customer, quantities,
                    cart.total(), paymentId, trackingNumber);
            orders.put(order.id(), order);
            notifications.send(customer.email(),
                    "Order " + order.id() + " confirmed. Tracking: " + trackingNumber);
            return order;

        } catch (SubsystemException failure) {
            System.out.println("   [Facade] step failed -> rolling back");
            rollback(undoActions);
            notifications.send(customer.email(), "We could not place your order: " + failure.getMessage());
            throw new OrderProcessingException("Order was not placed: " + failure.getMessage(), failure);
        }
    }

    public void cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new OrderProcessingException("Unknown order: " + orderId);
        }
        if (order.status() == OrderStatus.CANCELLED) {
            throw new OrderProcessingException("Order " + orderId + " is already cancelled");
        }
        shipping.cancel(order.trackingNumber());
        payments.refund(order.paymentId());
        inventory.restock(order.quantities());
        order.markCancelled();
        notifications.send(order.customer().email(), "Order " + orderId + " was cancelled and refunded.");
    }

    private void requireValid(Cart cart, Customer customer) {
        if (cart == null || cart.isEmpty()) {
            throw new OrderProcessingException("Cart is empty");
        }
        if (customer == null) {
            throw new OrderProcessingException("Customer is required");
        }
    }

    private void rollback(Deque<Runnable> undoActions) {
        while (!undoActions.isEmpty()) {
            undoActions.pop().run();
        }
    }
}
