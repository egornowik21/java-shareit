
## <a id="title1">ShareIt - сервис для шеринга</a> 
#### Сервис предоставляет возможность делиться вещами друг с другом, а также находить эти вещи и брать их в аренду.
<img width="740" alt="image" src="https://github.com/egornowik21/java-shareit/assets/114665170/c6a9a10a-008a-4920-9d78-b6f532ca2835">

------------
### <a id="title1">Технологии</a>
- Java 11
- Maven
- Spring Boot
- Hibernate
- PostgreSQL
- Slf4j
- Lombok
- REST API
- Docker
- Git
- Postman
- Junit, Mockito
---------
### <a id="title1">Быстрый старт</a>
Для начала нужно собрать проект с помощью Maven
```
mvn clean package
```
Далее разверните проект с помощью Docker Compose
```
docker-compose up
```
<img width="440" alt="image" src="https://github.com/egornowik21/java-shareit/assets/114665170/0514204a-0c9f-4524-826b-1a5abfbfc35b">

### <a id="title1">Функциональность приложения</a>
В приложении реализована 
### 1. Основной сервис (shareIt-server):
- [x] Добавление новой вещи;
- [X] Редактирование вещи;
- [X] Просмотр информации о конкретной вещи по её идентификатору;
- [X] Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой;
- [X] Поиск вещи потенциальным арендатором;
- [X] Добавление нового запроса на бронирование;
- [X] Подтверждение или отклонение запроса на бронирование;
- [X] Получение данных о конкретном бронировании;
- [X] Получение списка всех бронирований текущего пользователя;
- [X] Получение списка бронирований для всех вещей текущего пользователя;
- [X] Добавление комментариев от пользователей
- [X] Добавление запросов на аренду вещей и возможность принимать/отклонять их;
### 2. Шлюз (shareIt-gateway)
- [x] контроллеры, с которыми работают пользователи с валидацией входных данных;
- [x] кэширование повторяющихся запросов

### <a id="title1">Тестирование приложения</a>
- [x] Реализованы юнит-тесты для всего кода, содержащего логику
- [X] Реализовать интеграционные тесты, проверяющие взаимодействие с базой данных
- [X] Реализовать тесты для REST-эндпоинтов
- [X] Релизованы тесты для слоя репозиториев приложения с использованием аннотации @DataJpaTest
- [X] Реализованы тесты для работы с JSON для DTO
<img width="500" alt="image" src="https://github.com/egornowik21/java-shareit/assets/114665170/4b935944-2897-4e92-97a3-72be3b84c96e">

------------
### <a id="title1">Пример запроса POST</a>
```
http://localhost:9090/users
```
Request
```
{
    "name": "user",
    "email": "user@user.com"
}
```
Response
```
 {
    "id": 1,
    "name": "user",
    "email": "user@user.com"
}
```
### <a id="title1">Пример запроса PATCH</a>
```
http://localhost:9090/users/:userId
```
Request
```
{
    "name": "update",
    "email": "update@user.com"
}
```
Response

```
{
    "id": 1,
    "name": "update",
    "email": "update@user.com"
}
```

### <a id="title1">TODO</a>
- Разделение API на приватное, публичное и адмнистративное
- Добавление подборок вещей
- Добавление категорий для вещей
- Добавление Spring Security
