# RSO: Orders microservice

## Prerequisites

```bash
docker run -d --name tt-places-db -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=order -p 5433:5432 postgres:latest
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


## Build docker image
```bash
docker build . -t tt-places:X
```

[Optional] Define your own X

## Run application in Docker
```bash
docker run -p 8081:8081 tt-places:X
```
