# Supplier Delivery Service

## Описание

Backend-сервис для учёта поставок фруктов от поставщиков.

Сервис позволяет:

- создавать и получать поставщиков
- создавать и получать продукты
- изменять и удалять поставщиков и продукты
- хранить цены поставщиков по периодам действия
- рассчитывать стоимость позиции поставки по актуальной цене
- строить отчёт за выбранный период
- группировать данные отчёта по поставщику и продукту
- рассчитывать общий вес и общую стоимость

## Стек

- Java 25
- Spring Boot 4.1.1
- Spring MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- JUnit

## Структура данных

Основные сущности:

```text
Supplier
Product
SupplierPrice
Delivery
DeliveryItem
```

Связи:

```text
Supplier (1) ------ (*) SupplierPrice (*) ------ (1) Product
Supplier (1) ------ (*) Delivery
Delivery (1) ------ (*) DeliveryItem (*) ------ (1) Product
```

## Структура Backend

Исходный код Backend находится в:

```text
src/main/java/com/example/supplierdeliveryapp/
|-- controller/  - REST API
|-- service/     - бизнес-логика
|-- repository/  - доступ к PostgreSQL через Spring Data JPA
|-- entity/      - Hibernate-сущности, на основе которых формируются таблицы БД
|-- dto/         - модели JSON-запросов и ответов
|-- exception/   - исключения и обработка ошибок API
```

## Требования

Для запуска необходимы:

- JDK 25
- PostgreSQL
- Maven Wrapper из проекта

## Создание базы данных

Перед запуском приложения необходимо создать базу PostgreSQL:

```sql
CREATE DATABASE supplier_delivery;
```

## Переменные окружения

Необходимо задать переменные окружения:

```text
DB_USERNAME=postgres
DB_PASSWORD=<password>
```

Если `DB_USERNAME` не задан, используется пользователь `postgres`.

## Конфигурация приложения

Основные настройки находятся в:

```text
src/main/resources/application.properties
```

Пример:

```properties
spring.application.name=supplier-delivery-app

spring.datasource.url=jdbc:postgresql://localhost:5432/supplier_delivery
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

## Запуск приложения

Перед запуском задайте переменные окружения для текущей консоли:

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="<password>"
.\mvnw.cmd spring-boot:run
```

После запуска приложение доступно по адресу:

```text
http://localhost:8080/
```

## Web-интерфейс

- HTML
- CSS
- JavaScript
- Fetch API

Файлы frontend находятся в:

```text
src/main/resources/static/
|-- index.html
|-- style.css
|-- app.js
```

Через web-интерфейс можно:

- просматривать и добавлять поставщиков
- просматривать и добавлять продукты
- просматривать и добавлять цены поставщиков
- удалять добавленные позиции из формы поставки до отправки
- получать отчёт за выбранный период

Открыть frontend после запуска приложения:

```text
http://localhost:8080/
```

## API

### Поставщики

```text
POST   /api/suppliers
GET    /api/suppliers
PUT    /api/suppliers/{id}
DELETE /api/suppliers/{id}
```

### Продукты

```text
POST   /api/products
GET    /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

### Цены поставщиков

```text
POST /api/supplier-prices
GET  /api/supplier-prices
```

Пример:

```json
{
  "supplierId": 1,
  "productId": 1,
  "price": 150.00,
  "dateFrom": "2026-08-01",
  "dateTo": null
}
```

## Поставки

```text
POST /api/deliveries
```

Пример:

```json
{
  "supplierId": 1,
  "deliveryDate": "2026-08-30",
  "items": [
    {
      "productId": 1,
      "weight": 10.5
    },
    {
      "productId": 2,
      "weight": 5.0
    }
  ]
}
```

## Отчёт

```text
GET /api/reports/deliveries?dateFrom=2026-08-01&dateTo=2026-08-31
```

Отчёт группирует данные по `supplier + product` и рассчитывает:

- общий вес
- общую стоимость

Пример ответа:

```json
[
  {
    "supplierId": 1,
    "supplierName": "Поставщик 1",
    "productId": 1,
    "productName": "Яблоко Голден",
    "totalWeight": 30.5,
    "totalCost": 4575.00
  }
]
```

Если за указанный период поставок нет, возвращается:

```json
[]
```

## Проверка API

В корне проекта находится файл:

```text
requests.http
```

Он содержит последовательный сценарий проверки REST API независимо от web-интерфейса:

1. создание поставщиков
2. создание продуктов
3. получение справочников
4. создание цен
5. проверка смены цены
6. создание поставок
7. получение отчёта
8. обновление данных
9. проверки валидации
10. проверки ошибок
11. проверки удаления

## Проверка frontend

После запуска приложения откройте:

```text
http://localhost:8080/
```

Сценарий:

1. создать или выбрать поставщика
2. создать или выбрать продукт
3. добавить актуальную цену поставщика на продукт
4. создать поставку и добавить в неё один или несколько товаров
5. проверить сообщение об успешном создании поставки
6. построить отчёт за период, включающий дату поставки

## Автоматические тесты

Тесты находятся в:

```text
src/test/java/com/example/supplierdeliveryapp
```

Запуск:

```powershell
.\mvnw.cmd test
```

Основные integration-тесты покрывают:

- запуск Spring-контекста
- создание поставщика
- создание продукта
- создание поставки
- расчёт стоимости позиции
- создание новой цены
- автоматическое закрытие предыдущего периода цены
- агрегацию отчёта по весу и стоимости.

Для тестов используется профиль `test`.

Конфигурация тестов находится в:

```text
src/test/resources/application-test.properties
```

Рекомендуемый вариант:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/supplier_delivery
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
```

## Сборка проекта

Команда сборки по умолчанию запускает интеграционные-тесты. Перед сборкой должны быть запущены PostgreSQL и создана база данных `supplier_delivery`. Также необходимо задать переменные окружения `DB_USERNAME` и `DB_PASSWORD` в текущей консоли.

Собрать приложение:

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="<password>"
.\mvnw.cmd clean package
```
