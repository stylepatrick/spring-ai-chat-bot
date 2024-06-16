package org.example.springaichatbot.service;

import org.example.springaichatbot.resource.dto.BiggestCustomers;
import org.example.springaichatbot.resource.dto.CompanyHeadquarters;
import org.example.springaichatbot.resource.dto.WeatherResponse;
import org.example.springaichatbot.resource.dto.WeatherResponseMessage;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final OpenAiChatClient chatClient;
    private final OpenAiImageClient openAiImageClient;

    @Value("classpath:templates/get-customer-prompt-with-format.st")
    private Resource getCustomerPromptWithFormat;

    @Value("classpath:templates/get-company-headquarters-with-format.st")
    private Resource getGetCustomerPromptWithFormat;

    @Value("${weatherService.apiNinjasKey}")
    private String apiNinjasKey;

    @Value("classpath:templates/get-actual-weather-data.st")
    private Resource getActualWeatherData;


    public OpenAiService(OpenAiChatClient chatClient, OpenAiImageClient openAiImageClient) {
        this.chatClient = chatClient;
        this.openAiImageClient = openAiImageClient;
    }


    public String generateMessage(String message) {
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    public BiggestCustomers biggestCustomers(String company) {
        BeanOutputParser<BiggestCustomers> parser = new BeanOutputParser<>(BiggestCustomers.class);
        String format = parser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getCustomerPromptWithFormat);
        Prompt prompt = promptTemplate.create(Map.of("company", company, "format", format));
        ChatResponse response = chatClient.call(prompt);
        return parser.parse(response.getResult().getOutput().getContent());
    }

    public CompanyHeadquarters companyHeadquarters(String company) {
        BeanOutputParser<CompanyHeadquarters> parser = new BeanOutputParser<>(CompanyHeadquarters.class);
        String format = parser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getGetCustomerPromptWithFormat);
        Prompt prompt = promptTemplate.create(Map.of("company", company, "format", format));
        ChatResponse response = chatClient.call(prompt);
        return parser.parse(response.getResult().getOutput().getContent());
    }

    public WeatherResponseMessage getActualWeatherFromOpenAiFunction(String question) {
        OpenAiChatOptions promptOptions = OpenAiChatOptions.builder()
                .withFunctionCallbacks(List.of(FunctionCallbackWrapper.builder(new WeatherServiceFunction(apiNinjasKey))
                        .withName("CurrentWeather")
                        .withDescription("Get the current weather for a location")
                        .withResponseConverter((response) -> {
                            String schema = ModelOptionsUtils.getJsonSchema(WeatherResponse.class, false);
                            String json = ModelOptionsUtils.toJsonString(response);
                            return schema + "\n" + json;
                        })
                        .build()))
                .build();

        Message userMessage = new PromptTemplate(question).createMessage();

        Message systemMessage = new SystemPromptTemplate(getActualWeatherData).createMessage();

        ChatResponse response = chatClient.call(new Prompt(List.of(userMessage, systemMessage), promptOptions));

        return new WeatherResponseMessage(response.getResult().getOutput().getContent());
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
