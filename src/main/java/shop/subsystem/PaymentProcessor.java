package shop.subsystem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PaymentProcessor {
    private record Payment(String customerId, BigDecimal amount) { }

    private final Map<String, BigDecimal> balances = new HashMap<>();
    private final Map<String, Payment> payments = new HashMap<>();
    private int nextPayment = 1;

    public void topUp(String customerId, BigDecimal amount) {
        balances.merge(customerId, amount, BigDecimal::add);
    }

    public BigDecimal balanceOf(String customerId) {
        return balances.getOrDefault(customerId, BigDecimal.ZERO);
    }

    public String charge(String customerId, BigDecimal amount) throws PaymentDeclinedException {
        if (balanceOf(customerId).compareTo(amount) < 0) {
            throw new PaymentDeclinedException("Payment declined: insufficient funds (need " + amount
                    + ", have " + balanceOf(customerId) + ")");
        }
        balances.merge(customerId, amount.negate(), BigDecimal::add);
        String paymentId = "PAY-" + nextPayment++;
        payments.put(paymentId, new Payment(customerId, amount));
        System.out.println("   [Payment] charged " + amount + " -> " + paymentId);
        return paymentId;
    }

    public void refund(String paymentId) {
        Payment payment = payments.remove(paymentId);
        if (payment != null) {
            topUp(payment.customerId(), payment.amount());
            System.out.println("   [Payment] refunded " + payment.amount() + " (" + paymentId + ")");
        }
    }
}
