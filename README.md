jira-ticket-queue-plugin
========================
**Для проекта необходим JDK 6**

**Установка Atlassian SDK на Ubuntu**

***

* Скачиваем дистрибутив Atlassian SDK

`wget https://maven.atlassian.com/content/repositories/atlassian-public/com/atlassian/amps/atlassian-plugin-sdk/3.4/atlassian-plugin-sdk-3.4.zip`

* Распаковываем его в директорию /opt

`sudo unzip atlassian-plugin-sdk-3.4.zip -d /opt`

* Добавляем в переменную среды

`echo "export PATH=$PATH:/opt/atlassian-plugin-sdk-3.4/bin/" >> ~/.profile`


**Запуск тестов**

***

* Через run_tests.sh или вручную:

`atlas-clean`

`atlas-integration-test`
