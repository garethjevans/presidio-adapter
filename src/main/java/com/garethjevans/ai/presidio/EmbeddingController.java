package com.garethjevans.ai.presidio;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbeddingController {

    @Autowired
    private TextCleaner textCleaner;

    @PostMapping(path = "/embedding/prompt")
    public ResponseEntity<?> cleanRequest(@RequestBody OpenAiApi.EmbeddingRequest<String> request) {
        try {
            return ResponseEntity.ok(new OpenAiApi.EmbeddingRequest<String>(
                    textCleaner.clean(request.input()),
                    request.model(),
                    request.encodingFormat(),
                    request.dimensions(),
                    request.user())
            );
        } catch (BannedElementException exp) {
            return new ResponseEntity<String>(exp.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/embeddings/response")
    public ResponseEntity<OpenAiApi.ChatCompletion> cleanResponse(@RequestBody OpenAiApi.ChatCompletion response) {
        return ResponseEntity.ok(response);
    }
}