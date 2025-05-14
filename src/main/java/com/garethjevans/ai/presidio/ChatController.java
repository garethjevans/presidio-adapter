package com.garethjevans.ai.presidio;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private TextCleaner textCleaner;

    @PostMapping(path = "/chat/prompt")
    public ResponseEntity<?> cleanRequest(@RequestBody OpenAiApi.ChatCompletionRequest request) {
        try {
            List<OpenAiApi.ChatCompletionMessage> messages = request
                    .messages()
                    .stream()
                    .map(m -> new OpenAiApi.ChatCompletionMessage(textCleaner.clean(m.content()), m.role()))
                    .toList();

            return ResponseEntity.ok(new OpenAiApi.ChatCompletionRequest(
                    messages,
                    request.model(),
                    request.store(),
                    request.metadata(),
                    request.frequencyPenalty(),
                    request.logitBias(),
                    request.logprobs(),
                    request.topLogprobs(),
                    request.maxTokens(),
                    request.maxCompletionTokens(),
                    request.n(),
                    request.outputModalities(),
                    request.audioParameters(),
                    request.presencePenalty(),
                    request.responseFormat(),
                    request.seed(),
                    request.serviceTier(),
                    request.stop(),
                    request.stream(),
                    request.streamOptions(),
                    request.temperature(),
                    request.topP(),
                    request.tools(),
                    request.toolChoice(),
                    request.parallelToolCalls(),
                    request.user(),
                    request.reasoningEffort()));
        } catch (BannedElementException exp) {
            return new ResponseEntity<String>(exp.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/chat/response")
    public ResponseEntity<?> cleanResponse(@RequestBody OpenAiApi.ChatCompletion response) {
        try {
            List<OpenAiApi.ChatCompletion.Choice> choices = response
                    .choices()
                    .stream()
                    .map(m -> new OpenAiApi.ChatCompletion.Choice(m.finishReason(), m.index(), new OpenAiApi.ChatCompletionMessage(textCleaner.clean(m.message().content()), m.message().role()), m.logprobs()))
                    .toList();

            return ResponseEntity.ok(new OpenAiApi.ChatCompletion(
                    response.id(),
                    choices,
                    response.created(),
                    response.model(),
                    response.serviceTier(),
                    response.systemFingerprint(),
                    response.object(),
                    response.usage()));
        } catch (BannedElementException exp) {
            return new ResponseEntity<String>(exp.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/chat/response/stream")
    public ResponseEntity<?> cleanResponseChunk(@RequestBody OpenAiApi.ChatCompletionChunk response) {
        try {
            List<OpenAiApi.ChatCompletionChunk.ChunkChoice> choices = response
                    .choices()
                    .stream()
                    .map(m -> new OpenAiApi.ChatCompletionChunk.ChunkChoice(m.finishReason(), m.index(), new OpenAiApi.ChatCompletionMessage(textCleaner.clean(m.delta().content()), m.delta().role()), m.logprobs()))
                    .toList();

            return ResponseEntity.ok(new OpenAiApi.ChatCompletionChunk(
                    response.id(),
                    choices,
                    response.created(),
                    response.model(),
                    response.serviceTier(),
                    response.systemFingerprint(),
                    response.object(),
                    response.usage()));

        } catch (BannedElementException exp) {
            return new ResponseEntity<String>(exp.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}