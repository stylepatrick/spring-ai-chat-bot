package org.example.springaichatbot.resource;

import lombok.AllArgsConstructor;
import org.example.springaichatbot.resource.dto.BiggestCustomers;
import org.example.springaichatbot.resource.dto.CompanyHeadquarters;
import org.example.springaichatbot.resource.dto.RequestMessageDto;
import org.example.springaichatbot.resource.dto.WeatherResponseMessage;
import org.example.springaichatbot.service.OpenAiService;
import org.example.springaichatbot.service.VectorStoreService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@ApiRestController
@AllArgsConstructor
public class ApiResource {

    private final OpenAiService openAiService;
    private final VectorStoreService vectorStoreService;

    @PostMapping(value = "generalMessage")
    public ResponseEntity<String> generateMessage(
            @RequestBody RequestMessageDto requestMessageDto
    ) {
        return ResponseEntity.ok(openAiService.generateMessage(requestMessageDto.message()));
    }

    @PostMapping(value = "biggestCustomers")
    public ResponseEntity<BiggestCustomers> biggestCustomers(
            @RequestBody RequestMessageDto requestMessageDto
    ) {
        return ResponseEntity.ok(openAiService.biggestCustomers(requestMessageDto.message()));
    }

    @PostMapping(value = "companyHeadquarters")
    public ResponseEntity<CompanyHeadquarters> companyHeadquarters(
            @RequestBody RequestMessageDto requestMessageDto
    ) {
        return ResponseEntity.ok(openAiService.companyHeadquarters(requestMessageDto.message()));
    }

    @PostMapping(value = "weatherService")
    public ResponseEntity<WeatherResponseMessage> weatherService(
            @RequestBody RequestMessageDto requestMessageDto
    ) {
        return ResponseEntity.ok(openAiService.getActualWeatherFromOpenAiFunction(requestMessageDto.message()));
    }

    @PostMapping(value = "describeImage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> describeImage(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(openAiService.describeImage(file));
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

    @PostMapping(value ="talk", produces = "audio/mp3")
    public byte[] talkTalk(
            @RequestBody RequestMessageDto question
    ) {
        return openAiService.getSpeech(question.message());
    }

    @PostMapping(value ="describeImageWithSpeech", produces = "audio/mp3")
    public byte[] describeImageWithSpeech(
            @RequestParam("file") MultipartFile file
    ) {
        return openAiService.describeImageWithSpeech(file);
    }

    @PostMapping(value = "vectorstore/message")
    public ResponseEntity<String> generateVectorstoreMessage(
            @RequestBody RequestMessageDto requestMessageDto
    ) {
        return ResponseEntity.ok(vectorStoreService.messageFromVectorStore(requestMessageDto.message()));
    }

}
