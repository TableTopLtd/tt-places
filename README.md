# RSO: Orders microservice

## Prerequisites

```bash
docker run -d --name tt-places-db -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=place -p 5433:5432 postgres:latest
```

## When developing

Run
```bash
mvn clean package
```
from repo root directory.

To run application local jar, run:
```bash
java -jar api/target/*.jar
```

Microservice finds database through ip

To test the service you should go to
```
http://localhost:8081/v1/places
```
To see a list of all places.

```
http://localhost:8081/v1/places/1
```
To see the first one, etc.

## Build docker image
```bash
docker build . -t tt-places:X
```

[Optional] Define your own X

## Run application in Docker
```bash
docker run -p 8081:8081 tt-places:X
```
