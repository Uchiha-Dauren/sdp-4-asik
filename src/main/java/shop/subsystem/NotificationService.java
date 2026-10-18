package shop.subsystem;

public class NotificationService {
    public void send(String email, String message) {
        System.out.println("   [Notify] to " + email + ": " + message);
    }
}
