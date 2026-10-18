package shop;

import java.math.BigDecimal;
import shop.facade.OrderFacade;
import shop.facade.OrderProcessingException;
import shop.model.*;
import shop.subsystem.*;

public class Main {
    public static void main(String[] args) throws Exception {
        InventoryService inventory = new InventoryService();
        PaymentProcessor payments = new PaymentProcessor();
        ShippingService shipping = new ShippingService();
        NotificationService notifications = new NotificationService();
        OrderFacade facade = new OrderFacade(inventory, payments, shipping, notifications);

        inventory.addStock("LAPTOP", 5);
        inventory.addStock("MOUSE", 20);

        Customer dauren = new Customer("C1", "Dauren", "dauren@example.com", "Astana, Mangilik El 1");
        Customer noMoney = new Customer("C2", "Aigerim", "aigerim@example.com", "Almaty, Abay 10");
        payments.topUp("C1", new BigDecimal("2000.00"));
        payments.topUp("C2", new BigDecimal("50.00"));

        Cart cart = new Cart()
                .add(new CartItem("LAPTOP", "Laptop", 1, new BigDecimal("1200.00")))
                .add(new CartItem("MOUSE", "Mouse", 2, new BigDecimal("25.00")));

        section("1. Successful order via the facade");
        Order order = facade.placeOrder(cart, dauren);
        System.out.println("   => " + order);

        section("2. Payment declined -> stock is released (rollback)");
        System.out.println("   LAPTOP in stock before: " + inventory.available("LAPTOP"));
        try {
            facade.placeOrder(cart, noMoney);
        } catch (OrderProcessingException e) {
            System.out.println("   => " + e.getMessage());
        }
        System.out.println("   LAPTOP in stock after:  " + inventory.available("LAPTOP"));

        section("3. Out of stock");
        Cart bigCart = new Cart().add(new CartItem("LAPTOP", "Laptop", 50, new BigDecimal("1200.00")));
        try {
            facade.placeOrder(bigCart, dauren);
        } catch (OrderProcessingException e) {
            System.out.println("   => " + e.getMessage());
        }

        section("4. Cancel order");
        facade.cancelOrder(order.id());
        System.out.println("   => " + order + ", balance " + payments.balanceOf("C1"));

        section("5. Advanced client bypasses the facade (subsystem is still usable directly)");
        System.out.println("   MOUSE available: " + inventory.available("MOUSE"));
        notifications.send("admin@example.com", "Low-level notification without the facade");
    }

    private static void section(String title) {
        System.out.println("\n=== " + title + " ===");
    }
}
