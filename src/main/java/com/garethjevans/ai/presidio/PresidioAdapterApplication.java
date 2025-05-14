package com.garethjevans.ai.presidio;

import org.springframework.ai.model.openai.autoconfigure.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		OpenAiAudioSpeechAutoConfiguration.class,
		OpenAiAudioTranscriptionAutoConfiguration.class,
		OpenAiChatAutoConfiguration.class,
		OpenAiEmbeddingAutoConfiguration.class,
		OpenAiImageAutoConfiguration.class,
		OpenAiModerationAutoConfiguration.class})
public class PresidioAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PresidioAdapterApplication.class, args);
	}

}
