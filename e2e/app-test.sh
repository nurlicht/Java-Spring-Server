curl -X POST "localhost:8080/api/book" -H "X-API-KEY: API-KEY-RAW" -H "Content-Type: application/json" -d "{\"name\":\"name0\", \"publisher\":\"publisher0\", \"isbn\":\"isbn0\", \"language\":\"language0\", \"authors\":[\"author0a\", \"author0b\"]}"  | grep -q '^isbn0$'

curl -X GET "localhost:8080/api/books" -H "X-API-KEY: API-KEY-RAW" | grep -q '^name0$'

curl -X POST "localhost:8080/graphql" -H "Content-Type: application/json" -d "{\"query\": \"query GetBooksByLanguage {getBooksByLanguage(language: \\\"language0\\\") {id}}\"}" | grep -q '^publisher0$'

curl -X POST "localhost:8080/api/ai/chat" -H "X-API-KEY: API-KEY-RAW" -d "Where is the capital of South Africa"
