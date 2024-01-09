# Containerized Web App [![](https://github.com/nurlicht/Java-Spring-Server/actions/workflows/docker-compose.yml/badge.svg)](https://github.com/nurlicht/Java-Spring-Server/actions)

### Goal
Configuration and minimal code for a containerized web-app

### Main features
- Language: [Java](https://jdk.java.net/21/)
- Framework: [Spring](https://spring.io/) (AI, Security, Kafka, GraphQL, MVC, Data, Compose)
- Infrastructure: [Postgresql](https://www.postgresql.org/) (database) and [Apache Kafka](https://kafka.apache.org/) (streaming platform)
- No service layer (delegation of HTTP calls to the persistence layer or other clients)
- Automated [end-to-end tests](./e2e/app-test.sh) implemented as health-check of [containers](./app-client/docker-compose.yml) with a [GitHub-Action](./.github/workflows/docker-compose.yml)
    - Expected result: ```Container java-spring-server-spring-app-1  Healthy```
```
 Container db  Starting
 Container zookeeper  Starting
 Container db  Started
 Container zookeeper  Started
 Container kafka  Starting
 Container kafka  Started
 Container db  Waiting
 Container kafka  Waiting
 Container db  Healthy
 Container kafka  Healthy
 Container java-spring-server-spring-app-1  Starting
 Container java-spring-server-spring-app-1  Started
 Container java-spring-server-spring-app-1  Waiting
 Container java-spring-server-spring-app-1  Healthy
 Container java-spring-server-spring-app-client-1  Starting
 Container java-spring-server-spring-app-client-1  Started
compose started
```

### Modes of Operation
1. Complete containerization (infrastructure and Spring-app)
    - Relevant docker-compose file: [docker-compose.yml](./app-client/docker-compose.yml)
2. Containerization of the infrastructure + local execution of the Spring-app
    - Relevant docker-compose file: [docker-compose-infrastructure.yml](./infrastructure/docker-compose-infrastructure.yml)

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
    - ```docker-compose -f ./infrastructure/docker-compose-infrastructure.yml up```
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
    - ```docker-compose -f ./app-client/docker-compose.yml up```

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
    - ```curl -X GET -H "X-API-KEY: API-KEY-RAW" localhost:8080/api/books```
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

### Tests (GraphQL)
1. Use the embedded GraphQL GUI-client by opening ```http://localhost:8080/graphiql``` with a web-browser and then use the upper-left corner text-box to write your queries. Execute a query by pressing on the purple "Play" button:
    - Mutation
        ```
        mutation CreateBook {
            createBook(
                name: "name1"
                publisher: "publisher1"
                isbn: "isbn1"
                language: "language1"
                authors: [
                    "author1a"
                    "author1b"
                ]
            ) {
                id
                authors
            }
        }
        ```
    - Query
        ```
        query GetBooksByLanguage {
            getBooksByLanguage(language: "language1") {
                id
            }
        }
       ```
2. Alternatively (instead of the GUI-client) contact the GraphQL-server directly:
    - ```curl -X POST "localhost:8080/graphql" -H "Content-Type: application/json" -d "{\"query\": \"query GetBooksByLanguage {getBooksByLanguage(language: \\\"language1\\\") {id}}\"}"```

### Cleanup (at the very end and after all tests)
1. Stop all containers.
2. Run the command ```docker system prune --volumes --force```
3. Exit DockerDesktop.
4. Run the command ```wsl --shutdown```
