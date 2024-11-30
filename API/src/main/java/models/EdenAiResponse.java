package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdenAiResponse {
    @JsonProperty("public_id")
    private String publicId;
    private String status;
    private Map<String, EdenAiResult> results;

    public EdenAiResponse() {}

    public String getPublicId() {
        return publicId;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, EdenAiResult> getResults() {
        return results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EdenAiResult {
        private String text;

        public EdenAiResult() {
        }

        public String getText() {
            return text;
        }
    }
}
