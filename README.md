# Документация проекта OnlineGroceryAPI

## Описание проекта
Этот проект представляет собой REST API для интернет-магазина "Grocery". Он включает:
- Полный CRUD функционал для работы с товарами.
- Поддержку сохранения данных через PostgreSQL.
- Использование Docker для удобства развёртывания.
- Возможности фильтрации и сортировки товаров.

## Функционал
### CRUD для товаров:
- Получение списка товаров.
- Получение товара по ID.
- Создание нового товара.
- Обновление товара.
- Удаление товара.

### Фильтрация и сортировка:
- Фильтры:
  - По названию или части названия.
  - По цене (выше или ниже заданного значения).
  - По наличию товара.
- Сортировки:
  - По имени.
  - По цене.
- Ограничение количества выводимых записей.

## Примеры использования API
### CRUD операции
#### Получение списка товаров
```http
GET /api/v1/products
```
Пример ответа:
```json
[
  {
    "id": 1,
    "name": "Яблоки",
    "description": "Свежие красные яблоки.",
    "price": 120.0,
    "available": true
  },
  {
    "id": 2,
    "name": "Бананы",
    "description": "Спелые бананы.",
    "price": 150.0,
    "available": true
  }
]
```

#### Получение товара по ID
```http
GET /api/v1/products/{id}
```
Пример запроса:
```http
GET /api/v1/products/1
```
Пример ответа:
```json
{
  "id": 1,
  "name": "Яблоки",
  "description": "Свежие красные яблоки.",
  "price": 120.0,
  "available": true
}
```

#### Создание нового товара
```http
POST /api/v1/products
Content-Type: application/json
```
Пример тела запроса:
```json
{
  "name": "Молоко",
  "description": "Свежайшее молоко 1 л.",
  "price": 80.0,
  "available": true
}
```
Пример ответа:
```json
{
  "id": 3,
  "name": "Молоко",
  "description": "Свежайшее молоко 1 л.",
  "price": 80.0,
  "available": true
}
```

#### Обновление товара
```http
PUT /api/v1/products/{id}
Content-Type: application/json
```
Пример тела запроса:
```json
{
  "name": "Яблоки Зеленые",
  "description": "Свежие зелёные яблоки.",
  "price": 130.0,
  "available": true
}
```
Пример ответа:
```json
{
  "id": 1,
  "name": "Яблоки Зеленые",
  "description": "Свежие зелёные яблоки.",
  "price": 130.0,
  "available": true
}
```

#### Удаление товара
```http
DELETE /api/v1/products/{id}
```
Пример запроса:
```http
DELETE /api/v1/products/3
```
Пример ответа:
```json
{
  "message": "Товар с ID 3 успешно удалён."
}
```

### Фильтрация и сортировка
#### Пример фильтрации
```http
GET /api/v1/products?name=Яблоки&priceMin=100&available=true
```
Пример ответа:
```json
[
  {
    "id": 1,
    "name": "Яблоки Зеленые",
    "description": "Свежие зелёные яблоки.",
    "price": 130.0,
    "available": true
  }
]
```

#### Пример сортировки
```http
GET /api/v1/products?sort=name,asc
```
Пример ответа:
```json
[
  {
    "id": 2,
    "name": "Бананы",
    "description": "Спелые бананы.",
    "price": 150.0,
    "available": true
  },
  {
    "id": 1,
    "name": "Яблоки Зеленые",
    "description": "Свежие зелёные яблоки.",
    "price": 130.0,
    "available": true
  }
]
```

#### Ограничение количества записей
```http
GET /api/v1/products?limit=2
```
Пример ответа:
```json
[
  {
    "id": 1,
    "name": "Яблоки Зеленые",
    "description": "Свежие зелёные яблоки.",
    "price": 130.0,
    "available": true
  },
  {
    "id": 2,
    "name": "Бананы",
    "description": "Спелые бананы.",
    "price": 150.0,
    "available": true
  }
]
```

## Установка и запуск
### Локальный запуск
1. Установить Java 17, Maven, PostgreSQL.
2. Настроить файл `application.properties` для подключения к БД.
3. Запустить приложение:
   ```bash
   mvn spring-boot:run
