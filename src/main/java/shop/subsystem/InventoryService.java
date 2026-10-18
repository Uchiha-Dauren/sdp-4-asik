package shop.subsystem;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> stock = new HashMap<>();
    private final Map<String, Map<String, Integer>> reservations = new HashMap<>();
    private int nextReservation = 1;

    public void addStock(String sku, int quantity) {
        stock.merge(sku, quantity, Integer::sum);
    }

    public int available(String sku) {
        return stock.getOrDefault(sku, 0);
    }

   
    public String reserve(Map<String, Integer> items) throws OutOfStockException {
        for (var entry : items.entrySet()) {
            int have = available(entry.getKey());
            if (have < entry.getValue()) {
                throw new OutOfStockException(entry.getKey(), entry.getValue(), have);
            }
        }
        items.forEach((sku, qty) -> stock.merge(sku, -qty, Integer::sum));
        String reservationId = "RES-" + nextReservation++;
        reservations.put(reservationId, new HashMap<>(items));
        System.out.println("   [Inventory] reserved " + items + " as " + reservationId);
        return reservationId;
    }

    public void release(String reservationId) {
        Map<String, Integer> items = reservations.remove(reservationId);
        if (items != null) {
            items.forEach((sku, qty) -> stock.merge(sku, qty, Integer::sum));
            System.out.println("   [Inventory] released " + reservationId + " back to stock");
        }
    }

    public void commit(String reservationId) {
        reservations.remove(reservationId);
        System.out.println("   [Inventory] committed " + reservationId);
    }

    public void restock(Map<String, Integer> items) {
        items.forEach(this::addStock);
        System.out.println("   [Inventory] restocked " + items);
    }
}
