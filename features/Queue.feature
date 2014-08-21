#language: ru

Функционал: Очередь

  Сценарий: Создание очереди
    Допустим Я администратор
    И Создан проект "Test"
    И Я вхожу в управляющие группы
    И В проекте 10 тикетов
    Если Добавить очередь
    И Открыть список тикетов
    То Тикеты пронумерованы

  Сценарий: Закрытые тикеты не входят в очередь
    Дано В проекте 10 тикетов
    И Один из тикетов закрыт
    Если Добавить очередь
    И Открыть список тикетов
    То Закрытые тикеты не входят в очередь

  Сценарий: Перемещение тикетов вниз
    Дано В очереди 10 тикетов
    Если Открыть список тикетов
    И Переместить тикет с 1 места на 10 место
    То Нумерация тикетов в очереди изменяется

  Сценарий: Перемещение тикетов вверх
    Дано В очереди 10 тикетов
    Если Открыть список тикетов
    И Переместить тикет с 10 места на 1 место
    То Нумерация тикетов в очереди изменяется