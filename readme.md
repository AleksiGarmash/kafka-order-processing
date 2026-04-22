# Kafka Order Processing (2 приложения)

## Цель работы
Закрепить Kafka:
- Producer — отправка событий
- Consumer — прием и обработка
- 2 связанных приложения с обменом сообщениями

Архитектура
```text
POST /orders → order-service
↓ Kafka order-topic
order-status-service  
↓ Kafka order-status-topic
order-service (log в консоль)
```

**2 топика**:
- `order-topic` — `OrderEvent` (product, quantity)
- `order-status-topic` — `OrderStatusEvent` (status, date)
