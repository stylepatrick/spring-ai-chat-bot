package org.example.springaichatbot.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Service
@AllArgsConstructor
public class OpenAiService {

    private final OpenAiChatClient chatClient;
    private final OpenAiImageClient openAiImageClient;


    public Map<String, String> generateMessage(String message) {
        return Map.of("generation", chatClient.call(message));
    }

    public byte[] generateImage(String imageDescription) {
        ImageResponse response = openAiImageClient.call(
                new ImagePrompt(imageDescription,
                        OpenAiImageOptions.builder()
                                .withQuality("hd")
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024).build())

        );
        return downloadImage(response);
    }

    private byte[] downloadImage(ImageResponse imageResponse) {
        byte[] image;
        try {
            URL url = new URL(imageResponse.getResult().getOutput().getUrl());
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            image = out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
}
