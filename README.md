# RSO: Orders microservice

## Prerequisites

```bash
docker run -d --name rso-orders -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=order -p 5433:5432 postgres:latest
```

## Run application in Docker

```bash
docker run -p 8081:8081 jmezna/rso-orders
```