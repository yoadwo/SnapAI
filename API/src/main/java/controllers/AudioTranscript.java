package controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.EdenAiResponse;
import models.TranscriptRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Handler for requests to Lambda function.
 */
public class AudioTranscript implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    ObjectMapper objectMapper = new ObjectMapper();
    OkHttpClient client = new OkHttpClient.Builder()
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES).build();


    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        Map<String, String> headers = getHeaders();
        var response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        TranscriptRequest transcriptRequest;
        try {
            transcriptRequest = objectMapper.readValue(input.getBody(), TranscriptRequest.class);
        } catch (JsonProcessingException e) {
            return response
                    .withStatusCode(500)
                    .withBody("Failed to parse request body");
        }

        EdenAiResponse edenAiResponse;
        okhttp3.Call call = createEdenAiHttpCall(transcriptRequest);
        try (okhttp3.Response callResponse = call.execute()) {
            if (callResponse.body() == null) {
                return response
                        .withStatusCode(callResponse.code())
                        .withBody("Third-Party did not provide error");
            } else if (callResponse.code() != 200) {
                var errorResponse = callResponse.body().string();
                return response
                        .withStatusCode(callResponse.code())
                        .withBody(errorResponse);
            } else {
                edenAiResponse = objectMapper.readValue(callResponse.body().string(), EdenAiResponse.class);
            }
        } catch (JsonProcessingException e) {
            return response
                    .withStatusCode(500)
                    .withBody("Failed to parse request body");
        } catch (IOException e) {
            return response
                    .withStatusCode(500)
                    .withBody("Failed to call eden ai");
        }

        // possibly a job that was posted but not completed
        int status;
        String text;
        if (edenAiResponse.getStatus().equals("finished")) {
            status = 200;
            text = edenAiResponse.getResults().get(transcriptRequest.getAiProviderName()).getText();
        } else {
            status = 201;
            text = edenAiResponse.getPublicId();
        }

        return response
                .withStatusCode(status)
                .withBody(text);
    }

    private okhttp3.Call createEdenAiHttpCall(TranscriptRequest transcriptRequest) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("providers", String.join(",", transcriptRequest.getAiProviderName()))
                .addFormDataPart("file", transcriptRequest.getFileName(),
                        RequestBody.create(
                                Base64.getDecoder().decode(transcriptRequest.getFileContent().split(",")[1]),
                                MediaType.parse("audio/wav")))
                .addFormDataPart("language", "he")
                .addFormDataPart("profanity_filter", "false")
                .addFormDataPart("convert_to_wav", "false")
                .addFormDataPart("speakers", "1")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.edenai.run/v2/audio/speech_to_text_async")
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("content-type", requestBody.contentType().toString())
                .addHeader("authorization", "Bearer" + " " + transcriptRequest.getAiProviderKey())
                .build();

        return client.newCall(request);
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "*");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET,PUT,DELETE");
        return headers;
    }


}
