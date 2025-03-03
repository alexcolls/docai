# SmartDoc Api

## Prerequisites
Java 21

#### How to run Docker image for local PostgreSql database

```
$ docker pull postgres:latest 
$ docker run -itd -e POSTGRES_USER=smartdoc -e POSTGRES_PASSWORD=smartdoc -p 5432:5432 --name postgresql postgres
$ docker exec -it postgresql psql -U smartdoc -c "CREATE DATABASE dbinnocvsmartdocdev ENCODING 'UTF8'"
```

#### How to remove a local database
$ docker exec -it postgresql psql -U smartdoc -c "DROP DATABASE dbinnocvsmartdocdev"


## How to run
SPRING_PROFILES_ACTIVE=local;OPENAI_API_KEY=#OPENAI_API_KEY#;HOME=#LOCAL_DOCUMENT_FOLDER# ./gradlew bootRun

## How to run unit tests
./gradlew test


## Health check
GET /smartdoc-api/actuator/health

### Liveness

GET /smartdoc-api/actuator/health/liveness


### Swagger
GET /smartdoc-api/swagger-ui.html


### To access dev JobRunr dashboard

Connected to ASKInnocvAD run
```sh
kubectl port-forward svc/smartdoc-api 8001:8000 -n dev
```

then you can access the dashboard at
http://localhost:8001/dashboard/