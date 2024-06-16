package org.example.springaichatbot.service;

import org.example.springaichatbot.resource.dto.BiggestCustomers;
import org.example.springaichatbot.resource.dto.CompanyHeadquarters;
import org.example.springaichatbot.resource.dto.WeatherResponse;
import org.example.springaichatbot.resource.dto.WeatherResponseMessage;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final ChatModel chatModel;
    private final ImageModel imageModel;
    private final SpeechModel speechModel;

    @Value("classpath:templates/get-customer-prompt-with-format.st")
    private Resource getCustomerPromptWithFormat;

    @Value("classpath:templates/get-company-headquarters-with-format.st")
    private Resource getGetCustomerPromptWithFormat;

    @Value("${weatherService.apiNinjasKey}")
    private String apiNinjasKey;

    @Value("classpath:templates/get-actual-weather-data.st")
    private Resource getActualWeatherData;


    public OpenAiService(ChatModel chatModel, ImageModel imageModel, SpeechModel speechModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
        this.speechModel = speechModel;
    }


    public String generateMessage(String message) {
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    public BiggestCustomers biggestCustomers(String company) {
        BeanOutputConverter<BiggestCustomers> parser = new BeanOutputConverter<>(BiggestCustomers.class);
        String format = parser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getCustomerPromptWithFormat);
        Prompt prompt = promptTemplate.create(Map.of("company", company, "format", format));
        ChatResponse response = chatModel.call(prompt);
        return parser.convert(response.getResult().getOutput().getContent());
    }

    public CompanyHeadquarters companyHeadquarters(String company) {
        BeanOutputConverter<CompanyHeadquarters> parser = new BeanOutputConverter<>(CompanyHeadquarters.class);
        String format = parser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getGetCustomerPromptWithFormat);
        Prompt prompt = promptTemplate.create(Map.of("company", company, "format", format));
        ChatResponse response = chatModel.call(prompt);
        return parser.convert(response.getResult().getOutput().getContent());
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

        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage, systemMessage), promptOptions));

        return new WeatherResponseMessage(response.getResult().getOutput().getContent());
    }

    public String describeImage(MultipartFile file) {
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .withModel(OpenAiApi.ChatModel.GPT_4_VISION_PREVIEW.getValue())
                .build();

        UserMessage userMessage = null;
        try {
            userMessage = new UserMessage(
                    "Explain what do you see in this picture?",
                    List.of(new Media(MimeTypeUtils.IMAGE_JPEG, file.getBytes())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return chatModel.call(new Prompt(List.of(userMessage), chatOptions)).getResult().getOutput().toString();

    }

    public byte[] generateImage(String imageDescription) {
        ImageResponse response = imageModel.call(
                new ImagePrompt(imageDescription,
                        OpenAiImageOptions.builder()
                                .withQuality("hd")
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024).build())

        );
        return downloadImage(response);
    }

    public byte[] getSpeech(String text) {
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .withVoice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .withSpeed(1.0f)
                .withResponseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .withModel(OpenAiAudioApi.TtsModel.TTS_1.value)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(text,
                speechOptions);

        SpeechResponse response = speechModel.call(speechPrompt);

        return response.getResult().getOutput();
    }

    public byte[] describeImageWithSpeech(MultipartFile file) {
        String imageDescription = describeImage(file);
        return getSpeech(imageDescription);
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
