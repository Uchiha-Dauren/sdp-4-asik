# Clean Code principles used

1. Meaningful names: `placeOrder`, `cancelOrder`, `rollback`
2. Single Responsibility: the facade only coordinates, rules live in subsystem classes
3. No leaking of subsystem details: clients see `Order` and `OrderProcessingException` only
4. Small, focused subsystem classes: one responsibility each
5. Proper error handling: every completed step registers an undo action (rollback)
6. Dependency injection: services are passed through the facade constructor
