# Kafka Order Processing

Два микросервиса с асинхронной обработкой заказов через Apache Kafka.

## Стек технологий

| Компонент | Технологии |
|-----------|-----------|
| order-service | Java 17, Spring Boot, Kafka Producer |
| order-status-service | Java 17, Spring Boot, Kafka Consumer |
| Инфраструктура | Docker Compose (Kafka + Zookeeper) |
| Сериализация | JsonSerializer / JsonDeserializer |

## Архитектура

```
order-model/          — общие модели (Order, OrderEvent, OrderStatusEvent)
order-service/        — REST API + Kafka Producer
order-status-service/ — Kafka Consumer + обработка статусов
```

## Как запустить

### 1. Запустить Kafka

```bash
docker-compose up -d
```

### 2. Запустить сервисы (в разных терминалах)

```bash
# Терминал 1
cd order-service && mvn spring-boot:run

# Терминал 2
cd order-status-service && mvn spring-boot:run
```

### 3. Создать заказ

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productName": "Товар", "quantity": 2, "price": 1500.0}'
```

## Поток данных

```
Client → POST /api/orders
       → OrderService публикует OrderEvent в топик "orders"
       → OrderStatusService потребляет OrderEvent
       → Возвращает OrderStatusEvent в топик "order-status"
```

## Конфигурация Kafka

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```
