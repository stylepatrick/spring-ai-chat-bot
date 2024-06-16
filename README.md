# spring-ai-chat-bot

This project demonstrates various AI functionalities using Spring AI. It includes capabilities for generating chat responses, handling JSON formatted data, creating images, describing images, loading real-time data via APIs with Open AI Functions, generating speech from text, and utilizing a vector store for custom data.

### API POST-Request:

#### General Message to AI
http://localhost:8080/api/generalMessage

![img_1.png](img_1.png)

#### Get biggest customers for a given company and response in a JSON format

http://localhost:8080/api/biggestCustomers

![img_3.png](img_3.png)

#### Get headquaters for a given company and response in a JSON format

http://localhost:8080/api/companyHeadquarters

![img_4.png](img_4.png)

#### Generates an image from a given text with DALLE

http://localhost:8080/api/image

![img.png](img.png)

#### Use AI Function to load actual data to the request from an API

http://localhost:8080/api/weatherService

![img_5.png](img_5.png)

#### Use OpenAI vision to describe an Image

http://localhost:8080/api/describeImage

![img_6.png](img_6.png)

#### Generate a speech from a given text

http://localhost:8080/api/talk

![img_7.png](img_7.png)

#### Describe a given image with speech

http://localhost:8080/api/describeImageWithSpeech

![img_8.png](img_8.png)

#### Use a vector store to build embeddings on custom data

http://localhost:8080/vectorstore/message

![img_2.png](img_2.png)

## Vectorstore for custom data in PostgreSql

```
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vector_store (
id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
content text,
metadata json,
embedding vector(1536)
);

CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);
```
