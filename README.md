# Автоматизация тестирования формы заказа доставки карты

![Build Status](https://github.com/cybdar/selenide/actions/workflows/gradle.yml/badge.svg)

## Описание проекта
Проект содержит автотесты для формы заказа доставки карты с использованием Selenide.

## Сборка и запуск тестов
```bash
# Запуск всех тестов
./gradlew test

# Запуск в headless режиме
./gradlew test -Dselenide.headless=true