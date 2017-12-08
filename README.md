# movie-api
Spring Boot API that runs on top of elasticsearch. Swagger UI included.

## Prerequisite

A running elasticsearch cluster, with name `elasticsearch`.
All records ingested in indices `titles`, `names` and `mappings`

## Properties

Properties need to be set as VM args as shown below:

```
-Des.host=localhost
-Des.tcp.port=9300
```

Elasticsearch host and port is needed. This program will only communicate through Transport client of elasticsearch.

## Run

Run `Application.java` to run the program, swagger url would open in : `http://localhost:8080/netmdb/swagger-ui.html#/`