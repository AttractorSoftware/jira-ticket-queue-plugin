jira-ticket-queue-plugin
========================


[Инструкция по настройке](https://docs.google.com/document/d/1jEVr5BUOcixxWxmWC9OvZRj_ROhL3WB1neIUoO7ec5c/edit#) находится на Google Drive.

# Разработка примочки

**Для проекта необходим JDK 6**

**Установка Atlassian SDK на Ubuntu**

***

* Скачиваем дистрибутив Atlassian SDK

`wget https://maven.atlassian.com/content/repositories/atlassian-public/com/atlassian/amps/atlassian-plugin-sdk/4.2.20/atlassian-plugin-sdk-4.2.20.tar.gz`

* Распаковываем его в директорию /opt

`sudo tar -xzf atlassian-plugin-sdk-4.2.20.tar.gz -C /opt`

* Добавляем в переменную среды

`echo "export PATH=$PATH:/opt/atlassian-plugin-sdk-3.4/bin/" >> ~/.profile`


**Запуск тестов**

***

* Через run_tests.sh или вручную:

`atlas-clean`

`atlas-integration-test`
