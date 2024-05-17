# spring-ai-chat-bot
The project offers an API to generate a chat response on a given conversation and is able to generate pictures out of text. 
In addition it shows an example to use a vectorstore for custom data. 


#### API POST-Request:

http://localhost:8080/api/message

![img_1.png](img_1.png)

http://localhost:8080/api/image

![img.png](img.png)

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
