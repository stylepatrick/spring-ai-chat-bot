package org.example.springaichatbot.resource;

import lombok.AllArgsConstructor;
import org.example.springaichatbot.resource.dto.RequestMessageDto;
import org.example.springaichatbot.service.OpenAiService;
import org.springframework.ai.image.ImageResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@ApiRestController
@AllArgsConstructor
public class ApiResource {

    private final OpenAiService openAiService;

    @PostMapping(value = "message")
    public ResponseEntity<Map<String, String>> generateMessage(
            @RequestBody RequestMessageDto requestMessageDto
    ) {
        return ResponseEntity.ok(openAiService.generateMessage(requestMessageDto.message()));
    }

    @PostMapping(value = "image")
    public HttpEntity<byte[]> generateImage(
            @RequestBody RequestMessageDto requestMessageDto
    ) {
        byte[] image = openAiService.generateImage(requestMessageDto.message());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);

        return new HttpEntity<>(image, headers);
    }
}
