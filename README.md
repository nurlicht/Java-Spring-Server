# Web Application with Containerized Infrastructure
- Language: Java
- Framework: Spring
- Infrastructure: Postgresql database, (docker-compose)

### Dependencies
- Java 21
- Apache Maven 3.6.0

### Execution
1. Start [DockerDesktop](https://www.docker.com/products/docker-desktop/).
2. Start the infrastructure (Postgresql):
    - ```docker-compose -f docker-compose-infrastructure.yml up```
    - Verify the correct start of the database. The log should show:
        - ...
        - ```listening on IPv4 address "0.0.0.0", port 5432```
        - ...
        - ```database system is ready to accept connections```
3. Build and start the Spring project:
    - ```mvn -f ./demo clean package```
    - ```java -jar ./demo/target\demo-0.0.1-SNAPSHOT.jar```

### Tests (database)
1. Use CLI of the db container to query the database content directly:
```psql -U compose-postgres -c '\x' -c 'SELECT * FROM book;'```
2. Use the API to create new database entries or fetch existing entries:
- ```curl -X GET   localhost:8080/api/books```
- ```curl -X POST "localhost:8080/api/book" -H "Content-Type: application/json" -d "{\"name\":\"name0\", \"publisher\":\"publisher0\", \"isbn\":\"isbn0\", \"language\":\"language0\", \"authors\":[\"author0a\", \"author0b\"]}"```
    - Replace <i>name0</i> and other values with appropriate ones.
