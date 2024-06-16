# spring-ai-chat-bot
The project offers an API to generate a chat response on a given conversation and is able to generate pictures out of text. 
In addition it shows an example to use a vectorstore for custom data.
There are some .st templates used to generate the question and responses in a defined format with POJO classes.

To demonstrate the usage of OpenAiFunction an endpoint /weatherService is available which shows how to load actual data from an API, with the request and response build from the model, and give them to the OpenAi to generate the final response.

With OpenAI vision it is possible to let AI describe the content of a picture. To demonstrate it, there is a endpoint /describeImage available. 

#### API POST-Request:

http://localhost:8080/api/generalMessage

![img_1.png](img_1.png)

http://localhost:8080/api/biggestCustomers

![img_3.png](img_3.png)

http://localhost:8080/api/companyHeadquarters

![img_4.png](img_4.png)

http://localhost:8080/api/image

![img.png](img.png)

http://localhost:8080/api/weatherService

![img_5.png](img_5.png)

http://localhost:8080/api/describeImage

![img_6.png](img_6.png)

http://localhost:8080/api/talk
![img_7.png](img_7.png)

http://localhost:8080/api/describeImageWithSpeech
![img_8.png](img_8.png)

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
