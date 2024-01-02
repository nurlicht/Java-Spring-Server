# Containerized Web App [![](https://github.com/nurlicht/Java-Spring-Server/actions/workflows/docker-compose.yml/badge.svg)](https://github.com/nurlicht/Java-Spring-Server/actions)

### Goal
Configuration and minimal code for a containerized web-app

### Main features
- Language: [Java](https://jdk.java.net/21/)
- Framework: [Spring](https://spring.io/) (AI, Compose, Security, Kafka, MVC, Data)
- Infrastructure: [Postgresql](https://www.postgresql.org/) (database) and [Apache Kafka](https://kafka.apache.org/) (streaming platform)
- No service layer (delegation of HTTP calls to the persistence layer or other clients)
    - Several end-to-end tests (instead of unit- or integration-tests)
        - Enforcing tests as health-check tests of [containers](./docker-compose.yml)
        - Automatic start of containers with a [GitHub-Action](./.github/workflows/docker-compose.yml).

### Modes of Operation
1. Complete containerization (infrastructure and Spring-app)
    - Relevant docker-compose file: [docker-compose.yml](./docker-compose.yml)
2. Containerization of the infrastructure + local execution of the Spring-app
    - Relevant docker-compose file: [docker-compose-infrastructure.yml](./docker-compose-infrastructure.yml)

### Dependencies (only for local execution)
    - Java 21 (for Java 17, change the setting in [the build script](./pom.xml))
    - Apache Maven 3.6.0

### Execution (local)
1. Launch 5 terminals (CLI):
    - For the containerized infrastructure
    - For the Java-Spring application
    - For a client of the OpenAI API (CURL commands)
    - For a client of the own API (CURL commands)
    - For an independent Kafka subscriber
2. Set ```spring.kafka.bootstrap-servers=localhost:9092``` in [application.properties](./src/main/resources/application.properties)
3. Start [DockerDesktop](https://www.docker.com/products/docker-desktop/).
4. Start the infrastructure (Postgresql):
    - ```docker-compose -f docker-compose-infrastructure.yml up```
    - Verify the correct start of the database. The log should show:
        - ...
        - ```listening on IPv4 address "0.0.0.0", port 5432```
        - ...
        - ```database system is ready to accept connections```
5. Build and start the Spring project:
    - ```mvn clean package```
    - ```java -jar ./target\demo-0.0.1-SNAPSHOT.jar```

### Execution (complete containerization)
1. Launch 5 terminals (CLI):
    - For the containerized infrastructure
    - For the Java-Spring application
    - For a client of the OpenAI API (CURL commands)
    - For a client of the own API (CURL commands)
    - For an independent Kafka subscriber
2. Start [DockerDesktop](https://www.docker.com/products/docker-desktop/).
3. Start the infrastructure (Postgresql):
    - ```docker-compose up```

### Tests (AI)
1. Get an OpenAI API-key from [here](https://platform.openai.com/api-keys).
2. Use this API-key to set the corresponding property in [application.properties](./src/main/resources/application.properties).
3. Have the OpenAI API (ChatGPT) generate responses for your prompts:
    - ```curl -X POST "localhost:8080/api/ai/chat" -H "X-API-KEY: API-KEY-RAW" -d "Where is the capital of South Africa"```
        - Possible errors
            - 401 Unauthorized (invalid or no API-key) 
            - 429 Too Many Requests (valid API-key with insufficient credit) 

### Tests (database)
1. Use CLI of the db container to query the database content directly:
    - ```psql -U compose-postgres -c '\x' -c 'SELECT * FROM book;'```
2. Use the API to create new database entries or fetch existing entries:
    - ```curl -X GET   localhost:8080/api/books```
    - ```curl -X POST "localhost:8080/api/book" -H "X-API-KEY: API-KEY-RAW" -H "Content-Type: application/json" -d "{\"name\":\"name0\", \"publisher\":\"publisher0\", \"isbn\":\"isbn0\", \"language\":\"language0\", \"authors\":[\"author0a\", \"author0b\"]}"```
        - Replace <i>name0</i> and other values with appropriate ones.

### Tests (kafka)
1. Start an independent (CLI) subscriber to the published topic and monitor the logs:
    - ```docker exec -it broker /bin/sh```
    - ```/bin/kafka-console-consumer --topic book --from-beginning --bootstrap-server localhost:9092```
2. Monitor published messages in Spring logs (triggered by the ```POST /book``` endpoint):
    - ```... com.example.demo.kafka.KafkaPublisher    : Sending value=Book(id=null, name=name0, publisher=publisher0, isbn=isbn0, language=language0, authors=[author0a, author0b]) ...```
    - ```... com.example.demo.kafka.KafkaPublisher    : Sending value=Book(id=null, name=name0, publisher=publisher0, isbn=isbn0, language=language0, authors=[author0a, author0b]) was completed.```
    - ```... com.example.demo.kafka.KafkaPublisher    : Sending value=Book(id=..., name=name0, publisher=publisher0, isbn=isbn0, language=language0, authors=[author0a, author0b]) ...```
    - ```... com.example.demo.kafka.KafkaPublisher    : Sending value=Book(id=..., name=name0, publisher=publisher0, isbn=isbn0, language=language0, authors=[author0a, author0b]) was completed.```
    - ```... com.example.demo.kafka.KafkaSubscriber   : Received value={"id":...,"name":"name0","publisher":"publisher0","isbn":"isbn0","language":"language0","authors":["author0a","author0b"]}.```

### Cleanup (at the very end and after all tests)
1. Run the command ```docker-compose down```
2. Run the command ```docker system prune --volumes --force```
3. Exit DockerDesktop.
4. Run the command ```wsl --shutdown```

### Notes
- The tests mentioned above have been used in the docker-compose files for health-check upon starting services. 