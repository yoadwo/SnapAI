package controllers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.TranscriptRequest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class AudioTranscriptTest {
  @Test
  public void successfulResponse() {
    APIGatewayProxyRequestEvent request = mockFileRequest();
    AudioTranscript audioTranscript = new AudioTranscript();
    APIGatewayProxyResponseEvent result = audioTranscript.handleRequest(request, null);
    assertEquals(200, result.getStatusCode().intValue());
  }

  private APIGatewayProxyRequestEvent mockFileRequest() {
    // Build multipart form-data payload
    StringBuilder requestBody = new StringBuilder();
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(Objects.requireNonNull(classLoader.getResource("base64audio.txt")).getFile());
      String content = new String(Files.readAllBytes(file.toPath()));
      var tr = new TranscriptRequest();
      tr.setFileContent(content);
      tr.setFileName("123.wav");
      tr.setAiProviderName("openai");
      tr.setAiProviderKey(System.getenv("API_KEY"));

      requestBody.append(new ObjectMapper().writeValueAsString(tr));
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    // Create the API Gateway event
      return new APIGatewayProxyRequestEvent()
              .withBody(requestBody.toString())
              .withIsBase64Encoded(false);
  }
}
