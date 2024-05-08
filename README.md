# Система учета времени выполнения методов

### Описание:

<p align="justify">
Необходимо разработать систему учета времени выполнения методов в приложении с использованием Spring AOP.
Система должна быть способна асинхронно логировать и анализировать данные о времени выполнения методов.
</p>

### Требования:

<p align="justify">
Создать аннотации @TrackTime и @TrackAsyncTime, которые можно применять к методам для отслеживания времени их выполнения.
Реализовать аспекты, используя Spring AOP, для асинхронного и синхронного отслеживания времени выполнения методов,
помеченных соответствующими аннотациями.
Создать сервис, который будет асинхронно сохранять данные о времени выполнения методов в базе данных.
Реализовать REST API для получения статистики по времени выполнения методов (например, среднее время выполнения, общее время выполнения) для различных методов и их групп.
Настроить приложение с помощью конфигурации Spring для включения использования AOP и асинхронной обработки данных.
</p>

## Технологии/зависимости

В рамках проекта используется:<br>
[![Java21](https://img.shields.io/badge/JAVA-21-blue.svg)](https://adoptium.net/download/)
[![Spring Boot v3](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring&logoColor=white)](https://spring.io/projects/spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36.svg?style=flat-square&logo=Apache-Maven&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C.svg?style=flat-square&logo=Hibernate&logoColor=white)

### Среда разработки

Можно
использовать [JetBrains IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
или [VS Code](https://code.visualstudio.com/), либо любую другую совместимую с
вышеуказанными технологиями среду.

### База данных

В качестве БД в проекте используется PostgreSQL.<br>
Скачать БД можно с [официального сайта](https://www.postgresql.org/) или
воспользоваться [Docker](https://www.docker.com/) для развёртывания.

## Основная структура проекта

* `AopTrackTimeApplication` - точка входа в приложение, запуск приложения
  осуществляется с помощью метода main,
* `aspect/*` - классы и интерфейсы, связанные с AOP,
* `aspect/annotation/*` - аннотации AOP,
* `aspect/impl/*` - имплементация логики работы AOP,
* `entity/*` - классы-сущности для работы с репозиториями,
* `generator/*` - классы-генераторы сущностей,
* `repository/*` - интерфейсы и классы-репозитория для непосредственной
  манипуляции с хранилищами данных,
* `rest/*` - классы для работы REST-сервиса,
* `rest/advice/*` - классы для обработки ошибок при работе с REST-сервисом
* `rest/config/OpenApiConfig.java` - класс-конфигурации для настройки Swagger-UI
* `rest/controller/TrackTimeRestController.java` - класс REST-контроллера
* `rest/converter/*` - классы-конвертеры из данных JSON в экземпляры Java
* `rest/mapper/*` - классы для трансформации данных из одного вида в другой
* `rest/model/*` - классы, представляющие собой DTO для передачи данных
* `service/*` - классы-сервисы для работы с репозиториями,
* `utils/ThreadUtils.java` - вспомогательный класс для работы с потоками

## Запуск приложения

Для запуска приложения, необходимо скачать его:

```
git clone git@github.com:mshamanov/spring_aop_tracktime.git
```

Затем необходимо настроить БД.
Для этого нужно внести изменения в файл:
***src/main/resources/application.yaml***.

Укажите адрес БД, а также имя и пароль пользователя:

```
datasource:
url: jdbc:postgresql://localhost:5432/postgres
username: {ПОЛЬЗОВАТЕЛЬ}
password: {ПАРОЛЬ}
```

Название таблицы по умолчанию: tracktimestats<br>
Структура БД:

```postgresql
create table tracktimestats
(
    id             bigserial primary key,
    class_name     varchar(255) not null,
    created_at     timestamp(6) not null,
    execution_time bigint       not null,
    group_name     varchar(255) not null,
    method_name    varchar(255) not null,
    package_name   varchar(255) not null,
    parameters     varchar(255) not null,
    return_type    varchar(255) not null,
    method_status  varchar(255) not null
);
```

Создавать структуру, как правило, нет необходимости, т.к. при первом запуске
приложения структура будет создана благодаря
работе [Hibernate](https://hibernate.org/).

В том случае, если запуск осуществляется непосредственно из среды
разработки, то Вы можете просто запустить метод `main`
из `com.mash.aoptracktime.AopTrackTimeApplication`.

Другим способо будет установка через Maven:

Если Maven уже установлен на Вашем компьютере, то Вы можете просто запустить:

```
mvn spring-boot:run
```

Если Maven не установлен на Вашем компьютере, то Вы можете запустить следующую
команду:

```
./mvnw spring-boot:run
```

При успешном запуске приложения будет запущен класс генерирующий произвольные
данные о сотрудниках, сохраняя эти данные в БД [**100 записей**].
Методы, генерирующие данные о сотрдуниках, а также методы сохранения данных в
БД, помечены аннотациями `@TrackTime` и `@TrackAsyncTime` в зависимости от того
является ли вызов метода синхронным или асинхронным. При этом методы сохранения
в БД данных о сотрудниках имитируют небольшую задержку в пределах 300мс для
того,
чтобы создать разное время выполнения одних и тех же методов.
Некоторые методы могут получить статус `EXCEPTION`, это не означает неправильную
работу приложения, это сделано для разнообразия примеров. Вся метрика вызова
методов сохраняется в БД, в таблице `tracktimestats`.

Таким образом, используя аннотации `@TrackTime` и `@TrackAsyncTime` мы можем оценить
время работы тех или иных методов в рамках приложения Spring Boot, используя
возможности AOP. Для дальнейшей оценки этих данных мы можем сделать выборку через
обращение к REST-сервису и получения необходимых данных по различным критериям
выборки.

### Взаимодействие с REST-сервисом

Для того, чтобы получить выборку сформированных данных, Вы можете
воспользоваться такими инструментами, как [Postman](https://www.postman.com/) или воспользовательской
пользовательской средой Swagger-UI для работы с REST-сервисом:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Там же Вы можете найти возможные запросы и ответы от сервера.

## Лицензия

[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](https://opensource.org/licenses/MIT)