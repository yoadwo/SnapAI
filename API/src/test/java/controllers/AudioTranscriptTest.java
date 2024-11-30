package controllers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class AudioTranscriptTest {
  @Test
  public void successfulResponse() {
    APIGatewayProxyRequestEvent request = mockFileRequest();
    AudioTranscript audioTranscript = new AudioTranscript();
    APIGatewayProxyResponseEvent result = audioTranscript.handleRequest(request, null);
    assertEquals(200, result.getStatusCode().intValue());
    assertEquals("application/json", result.getHeaders().get("Content-Type"));
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"hello world\""));
    assertTrue(content.contains("\"location\""));
  }

  private APIGatewayProxyRequestEvent mockFileRequest() {
    // Create a multipart form-data boundary
    String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";

    // Simulate two blobs of data
    byte[] fileBlob = "mock file content".getBytes(StandardCharsets.UTF_8);
    byte[] secondBlob = "mock blob content".getBytes(StandardCharsets.UTF_8);

    // Build multipart form-data payload
    StringBuilder requestBody = new StringBuilder();
    requestBody.append("--").append(boundary).append("\r\n");
    requestBody.append("Content-Disposition: form-data; name=\"file\"; filename=\"test.txt\"\r\n");
    requestBody.append("Content-Type: application/octet-stream\r\n\r\n");
    requestBody.append(new String(fileBlob)).append("\r\n");

    requestBody.append("--").append(boundary).append("\r\n");
    requestBody.append("Content-Disposition: form-data; name=\"blob\"\r\n");
    requestBody.append("Content-Type: application/octet-stream\r\n\r\n");
    requestBody.append(new String(secondBlob)).append("\r\n");

    requestBody.append("--").append(boundary).append("--\r\n");

    // Create headers
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "multipart/form-data; boundary=" + boundary);

    // Create the API Gateway event
    APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
            .withHeaders(headers)
            .withBody(requestBody.toString())
            .withIsBase64Encoded(false);

    return request;
  }
}
