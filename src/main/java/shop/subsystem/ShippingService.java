package shop.subsystem;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShippingService {
    private final Set<String> activeShipments = new HashSet<>();
    private int nextTracking = 1;

    public String schedule(String address, Map<String, Integer> items) throws ShippingException {
        if (address == null || address.isBlank()) {
            throw new ShippingException("Cannot schedule shipping: delivery address is missing");
        }
        String tracking = "TRK-" + nextTracking++;
        activeShipments.add(tracking);
        System.out.println("   [Shipping] scheduled " + tracking + " to " + address);
        return tracking;
    }

    public void cancel(String trackingNumber) {
        if (activeShipments.remove(trackingNumber)) {
            System.out.println("   [Shipping] cancelled " + trackingNumber);
        }
    }

    public boolean isActive(String trackingNumber) {
        return activeShipments.contains(trackingNumber);
    }
}
