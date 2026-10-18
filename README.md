Assignment #4 — **Facade** pattern, ShP-2216 Software Design Patterns.

`OrderFacade` hides four collaborating services behind two simple methods:

```java
Order order = facade.placeOrder(cart, customer);
facade.cancelOrder(order.id());
```

Behind `placeOrder` the facade reserves stock, charges the payment, schedules shipping, commits the
reservation and notifies the customer. If any step fails, the completed steps are **rolled back**
(stock released, payment refunded, shipment cancelled).


## Pattern notes

* Subsystem classes never reference `OrderFacade` and can be used directly (see scenario 5 in `Main`).
* The facade receives the services through its constructor, so advanced clients share the same instances.
* Clients only need to catch one exception type: `OrderProcessingException`.
# sdp-4-asik
