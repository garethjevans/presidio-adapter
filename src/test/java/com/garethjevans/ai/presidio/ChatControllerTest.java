package com.garethjevans.ai.presidio;

import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void canAnonymizeRequest() throws Exception {
        OpenAiApi.ChatCompletionRequest r = new OpenAiApi.ChatCompletionRequest(List.of(new OpenAiApi.ChatCompletionMessage("Hello, my name is James Bond", OpenAiApi.ChatCompletionMessage.Role.USER)), false);

        OpenAiApi.ChatCompletionRequest response =
                this.restTemplate.postForObject("http://localhost:" + port + "/chat/prompt",
                 new HttpEntity<OpenAiApi.ChatCompletionRequest>(r),
                        OpenAiApi.ChatCompletionRequest.class);

        assertThat(response).isNotNull();
        assertThat(response.messages()).hasSize(1);
        assertThat(response.messages().getFirst().content()).doesNotContain("James Bond");
    }

}
