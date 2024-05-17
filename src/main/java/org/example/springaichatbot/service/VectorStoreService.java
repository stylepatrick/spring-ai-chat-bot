package org.example.springaichatbot.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        List<Document> documents = List.of(
                new Document("I was born in Italy on May 1990"),
                new Document("I have two sisters and one brother"),
                new Document("My hobbies are hiking."));
                new Document("I like Spring Boot");

        // Add the documents to PGVector
        vectorStore.add(documents);
    }

    public String messageFromVectorStore(String message) {
        // Retrieve documents similar to a query
        List<Document> results = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(5));
        if (results.isEmpty()) {
            return null;
        }
        return results.getFirst().getContent();
    }
}
