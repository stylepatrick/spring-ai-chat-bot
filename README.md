# Spring AI Chat Bot

This project demonstrates various AI functionalities using Spring AI. It includes capabilities for generating chat responses, handling JSON formatted data, creating images, describing images, loading real-time data via APIs with Open AI Functions, generating speech from text, and utilizing a vector store for custom data.

### API POST-Requests:

#### General Message to AI
- **URL**: `http://localhost:8080/api/generalMessage`
- **Description**: Sends a general message to AI for a response.
![General Message](img_1.png)

#### Get Biggest Customers
- **URL**: `http://localhost:8080/api/biggestCustomers`
- **Description**: Retrieves the biggest customers for a given company in JSON format.
![Biggest Customers](img_3.png)

#### Get Headquarters for a Company
- **URL**: `http://localhost:8080/api/companyHeadquarters`
- **Description**: Gets the headquarters of a given company in JSON format.
![Company Headquarters](img_4.png)

#### Generate an Image from Text
- **URL**: `http://localhost:8080/api/image`
- **Description**: Generates an image from a given text using DALLE.
![Image Generation](img.png)

#### Load Real-Time Data via API
- **URL**: `http://localhost:8080/api/weatherService`
- **Description**: Uses AI functions to load actual data from an API request and generate a response.
![Weather Service](img_5.png)

#### Describe an Image
- **URL**: `http://localhost:8080/api/describeImage`
- **Description**: Uses OpenAI vision to describe the content of an image.
![Image Description](img_6.png)

#### Generate Speech from Text
- **URL**: `http://localhost:8080/api/talk`
- **Description**: Generates speech from a given text.
![Text to Speech](img_7.png)

#### Describe an Image with Speech
- **URL**: `http://localhost:8080/api/describeImageWithSpeech`
- **Description**: Describes an image with generated speech.
![Image Description with Speech](img_8.png)

#### Use a Vector Store for Custom Data
- **URL**: `http://localhost:8080/vectorstore/message`
- **Description**: Uses a vector store to build embeddings on custom data.
![Vector Store Message](img_2.png)

## Vector Store Setup in PostgreSQL

```sql
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
