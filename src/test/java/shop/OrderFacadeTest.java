package shop;

import shop.facade.OrderFacade;
import shop.facade.OrderProcessingException;
import shop.model.*;
import shop.subsystem.*;

import java.math.BigDecimal;


public class OrderFacadeTest {
    private static int passed = 0;

    public static void main(String[] args) {
        successfulOrderReducesStockAndCharges();
        declinedPaymentReleasesStock();
        shippingFailureRollsBackPaymentAndStock();
        cancelRefundsAndRestocks();
        emptyCartIsRejected();
        System.out.println("\nAll " + passed + " tests passed.");
    }

    private static Fixture fixture() {
        Fixture f = new Fixture();
        f.inventory.addStock("A", 10);
        f.payments.topUp("C1", new BigDecimal("100"));
        return f;
    }

    private static void successfulOrderReducesStockAndCharges() {
        Fixture f = fixture();
        Order o = f.facade.placeOrder(cart(2), f.customer);
        check(f.inventory.available("A") == 8, "stock reduced");
        check(f.payments.balanceOf("C1").compareTo(new BigDecimal("80")) == 0, "customer charged");
        check(f.shipping.isActive(o.trackingNumber()), "shipment active");
        pass("successfulOrderReducesStockAndCharges");
    }

    private static void declinedPaymentReleasesStock() {
        Fixture f = fixture();
        try {
            f.facade.placeOrder(cart(20), f.customer); // costs 200, balance 100
            check(false, "expected failure");
        } catch (OrderProcessingException e) { /* expected */ }
        check(f.inventory.available("A") == 10, "stock released");
        check(f.payments.balanceOf("C1").compareTo(new BigDecimal("100")) == 0, "no charge");
        pass("declinedPaymentReleasesStock");
    }

    private static void shippingFailureRollsBackPaymentAndStock() {
        Fixture f = fixture();
        Customer noAddress = new Customer("C1", "X", "x@example.com", "");
        try {
            f.facade.placeOrder(cart(2), noAddress);
            check(false, "expected failure");
        } catch (OrderProcessingException e) { /* expected */ }
        check(f.inventory.available("A") == 10, "stock released");
        check(f.payments.balanceOf("C1").compareTo(new BigDecimal("100")) == 0, "payment refunded");
        pass("shippingFailureRollsBackPaymentAndStock");
    }

    private static void cancelRefundsAndRestocks() {
        Fixture f = fixture();
        Order o = f.facade.placeOrder(cart(3), f.customer);
        f.facade.cancelOrder(o.id());
        check(o.status() == OrderStatus.CANCELLED, "status cancelled");
        check(f.inventory.available("A") == 10, "restocked");
        check(f.payments.balanceOf("C1").compareTo(new BigDecimal("100")) == 0, "refunded");
        check(!f.shipping.isActive(o.trackingNumber()), "shipment cancelled");
        pass("cancelRefundsAndRestocks");
    }

    private static void emptyCartIsRejected() {
        Fixture f = fixture();
        try {
            f.facade.placeOrder(new Cart(), f.customer);
            check(false, "expected failure");
        } catch (OrderProcessingException e) {}
        pass("emptyCartIsRejected");
    }

    private static Cart cart(int qty) {
        return new Cart().add(new CartItem("A", "Item A", qty, new BigDecimal("10")));
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError("FAILED: " + message);
    }

    private static void pass(String name) {
        passed++;
        System.out.println("PASS " + name);
    }

    private static class Fixture {
        final InventoryService inventory = new InventoryService();
        final PaymentProcessor payments = new PaymentProcessor();
        final ShippingService shipping = new ShippingService();
        final NotificationService notifications = new NotificationService();
        final OrderFacade facade = new OrderFacade(inventory, payments, shipping, notifications);
        final Customer customer = new Customer("C1", "Test", "t@example.com", "Astana");
    }
}
