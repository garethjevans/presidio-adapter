package com.garethjevans.ai.presidio;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class TextCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextCleaner.class);

    @Value("${presidio.threshold:0.5}")
    private Double threshold;

    @Value("${presidio.bannedElements:[]}")
    private List<String> bannedElements;

    @Value("${presidio.allowedElements:[]}")
    private List<String> allowedElements;

    @Value("${presidio.analyze.url}")
    private String analyzeUrl;

    @Value("${presidio.anonymize.url}")
    private String anonymizeUrl;

    public String clean(String text) {
        LOGGER.info("Cleaning text {}", text);

        RestClient client = RestClient.builder().build();

        List<AnalyzeResponse> analyzeResponses = client.post()
                .uri(analyzeUrl)
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .body(new AnalyzeRequest(text, "en"))
                .retrieve()
                .body(new ParameterizedTypeReference<List<AnalyzeResponse>>() {
                });

        LOGGER.info("AnalyzeResponse {}", analyzeResponses);

        // validate results
        List<AnalyzeResponse> bannedResults = analyzeResponses
                .stream()
                .filter(r -> r.score > threshold)
                .filter(r -> bannedElements.contains(r.entityType))
                .toList();

        if (!bannedResults.isEmpty()) {
            throw new BannedElementException("Message contains banned elements " + bannedResults.stream().map(r -> r.entityType).toList());
        }

        List<AnalyzeResult> results = analyzeResponses
                .stream()
                .filter(r -> !allowedElements.contains(r.entityType))
                .filter(r -> r.score > threshold)
                .map(r -> new AnalyzeResult(r.start(), r.end(), r.score(), r.entityType()))
                .toList();

        AnonymizeRequest anonymizeRequest = new AnonymizeRequest(text, results);

        AnonymizeResponse response = client.post()
                .uri(anonymizeUrl)
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .body(anonymizeRequest)
                .retrieve()
                .body(AnonymizeResponse.class);

        LOGGER.info("AnonymizeResponse {}", response);

        return response.text;
    }

    public record AnalyzeRequest(@JsonProperty("text") String text,
                                 @JsonProperty("language") String language) {}

    public record AnalyzeResponse(@JsonProperty("analysis_explanation") String analysisExplanation,
                                  @JsonProperty("start") Integer start,
                                  @JsonProperty("end") Integer end,
                                  @JsonProperty("score") Double score,
                                  @JsonProperty("entity_type") String entityType) {}

    public record AnonymizeRequest(@JsonProperty("text") String text,
                                   @JsonProperty("analyzer_results") List<AnalyzeResult> analyzeResults) {}

    public record AnalyzeResult(@JsonProperty("start") Integer start,
                                @JsonProperty("end") Integer end,
                                @JsonProperty("score") Double score,
                                @JsonProperty("entity_type") String entityType) {}

    public record AnonymizeResponse(@JsonProperty("text") String text) {}
}
