# Defense notes

- What does the facade hide? Call order, IDs between services, rollback, subsystem exceptions.
- What stays accessible? All subsystem classes can still be used directly.
- Why no back-references? The subsystem must work without the facade.
- Why a Deque of Runnables? Undo actions must run in reverse order.
