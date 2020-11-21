##Тестирование MealRestController

####Создать
```shell script
curl -X POST -H 'Content-Type: application/json' -d '{"dateTime":"2020-11-21T19:00:00","description":"Перекус","calories":2000}' http://localhost:8080/topjava/rest/meals
```
---
####Обновить запись
```shell script
curl -X PUT -v -H 'Content-Type: application/json' -d '{"id":100005,"dateTime":"2020-11-21T10:00:00","description":"Обед","calories":1500}' http://localhost:8080/topjava/rest/meals/100005
```
---
####Получить по id
```shell script
curl http://localhost:8080/topjava/rest/meals/100002
```
---
####Удалить
```shell script
curl -X DELETE -v http://localhost:8080/topjava/rest/meals/100007
```
---
####Получить всю еду
```shell script
curl http://localhost:8080/topjava/rest/meals/
```
---
####Отфильтровать по дате и времени
```shell script
curl 'http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&startTime=09:00:00&endDate=2020-01-30&endTime=18:00:00'
```
---
####Отфильтровать без параметров
```shell script
curl http://localhost:8080/topjava/rest/meals/filter
```