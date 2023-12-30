# Web Application with Containerized Infrastructure
- Language: Java
- Framework: Spring
- Infrastructure: Postgresql database, (docker-compose)

### Dependencies
- Java 21
- Apache Maven 3.6.0

### Execution
1. Launch 4 terminals (CLI):
    - For the containerized infrastructure
    - For the Java-Spring application
    - For an API client (CURL commands)
    - For an independent Kafka subscriber
2. Start [DockerDesktop](https://www.docker.com/products/docker-desktop/).
3. Start the infrastructure (Postgresql):
    - ```docker-compose -f docker-compose-infrastructure.yml up```
    - Verify the correct start of the database. The log should show:
        - ...
        - ```listening on IPv4 address "0.0.0.0", port 5432```
        - ...
        - ```database system is ready to accept connections```
4. Build and start the Spring project:
    - ```mvn -f ./demo clean package```
    - ```java -jar ./demo/target\demo-0.0.1-SNAPSHOT.jar```

### Tests (AI)
1. Get an OpenAI API-key from [here](https://platform.openai.com/api-keys).
2. Use this API-key to set the corresponding property in [application.properties](./demo/src/main/resources/application.properties).
3. Have the OpenAI API (ChatGPT) generate responses for your prompts:
    - ```curl -X POST "localhost:8080/api/ai/chat" -H "X-API-KEY: X-API-VALUE" -d "Where is the capital of South Africa"```
        - Possible errors
            - 401 Unauthorized (invalid or no API-key) 
            - 429 Too Many Requests (valid API-key with insufficient credit) 

### Tests (database)
1. Use CLI of the db container to query the database content directly:
    - ```psql -U compose-postgres -c '\x' -c 'SELECT * FROM book;'```
2. Use the API to create new database entries or fetch existing entries:
    - ```curl -X GET   localhost:8080/api/books```
    - ```curl -X POST "localhost:8080/api/book" -H "X-API-KEY: X-API-VALUE" -H "Content-Type: application/json" -d "{\"name\":\"name0\", \"publisher\":\"publisher0\", \"isbn\":\"isbn0\", \"language\":\"language0\", \"authors\":[\"author0a\", \"author0b\"]}"```
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
1. Stop (and delete) the containers.
2. Exit DockerDesktop.
3. Run the command ```wsl --shutdown```
