package shop.facade;


public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String message) { super(message); }

    public OrderProcessingException(String message, Throwable cause) { super(message, cause); }
}
