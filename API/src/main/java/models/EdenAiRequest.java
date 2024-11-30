package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public class EdenAiRequest {
    private final boolean showOriginalResponse;
    @JsonProperty("show_base_64")
    private final boolean showBase64;
    private final int speakers;
    private final boolean profanityFilter;
    private final boolean convertToWav;
    private final List<String> providers;
    private final String language;
    private final String customVocabulary;
    private final String file;

    public EdenAiRequest(String providers, String file) {
        this.showOriginalResponse = false;
        this.showBase64 = false;
        this.speakers = 1;
        this.profanityFilter = false;
        this.convertToWav = true;
        this.providers = List.of(providers);
        this.language = "he";
        this.customVocabulary = "";

        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        this.file = file;
    }
}
