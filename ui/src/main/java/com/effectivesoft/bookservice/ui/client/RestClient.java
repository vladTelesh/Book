package com.effectivesoft.bookservice.ui.client;

import com.effectivesoft.bookservice.ui.config.security.SecurityContextParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class RestClient {
    @Value("${request.server}")
    private String requestServer;
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
    private final ObjectMapper objectMapper;

    public RestClient(@Autowired ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    Optional<Response> getRequest(String link) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(requestServer + link);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(String.valueOf(HttpMethod.GET));

            setHeaders(connection);

            return Optional.of(readResponse(connection));
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        } finally {
            disconnect(connection);
        }

    }

    Optional<Response> postRequest(String link, Object obj) throws JsonProcessingException {
        String jsonBody = null;
        if (!(obj instanceof String)) {
            jsonBody = objectMapper.writeValueAsString(obj);
        } else {
            jsonBody = (String) obj;
        }
        HttpURLConnection connection = null;
        try {
            URL url = new URL(requestServer + link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(String.valueOf(HttpMethod.POST));

            setHeaders(connection);

            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes());
            }

            return Optional.of(readResponse(connection));
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        } finally {
            disconnect(connection);
        }
    }

    Optional<Response> postRequest(String link, String filename, String fileContentType, byte[] byteArray) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(requestServer + link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(String.valueOf(HttpMethod.POST));
            connection.setDoOutput(true);

            connection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=*****");

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                connection.setRequestProperty("Username", SecurityContextParser.getEmail());
            }

            DataOutputStream request = new DataOutputStream(connection.getOutputStream());
            request.write(byteArray);
            request.writeBytes("--" + "*****" + "\r\n");
            request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" +
                    filename + "\"\r\nContent-Type: " + fileContentType + "\r\n");
            request.writeBytes("\r\n");
            request.write(byteArray);
            request.writeBytes("\r\n");
            request.writeBytes("--" + "*****" +
                    "--" + "\r\n");

            return Optional.of(readResponse(connection));
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        } finally {
            disconnect(connection);
        }
    }

    Optional<Response> putRequest(String link, Object obj) throws JsonProcessingException {
        String jsonBody = null;
        if (!(obj instanceof String)) {
            jsonBody = objectMapper.writeValueAsString(obj);
        } else {
            jsonBody = (String) obj;
        }
        HttpURLConnection connection = null;
        try {
            URL url = new URL(requestServer + link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(String.valueOf(HttpMethod.PUT));

            setHeaders(connection);

            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes());
            }

            return Optional.of(readResponse(connection));
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        } finally {
            disconnect(connection);
        }
    }

    Optional<Response> deleteRequest(String link) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(requestServer + link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(String.valueOf(HttpMethod.DELETE));

            setHeaders(connection);

            return Optional.of(readResponse(connection));
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        } finally {
            disconnect(connection);
        }
    }


    private Response readResponse(HttpURLConnection connection) throws IOException {
        InputStream stream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder responseString = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            responseString.append(line);
            responseString.append('\r');
        }
        bufferedReader.close();

        Response response = new Response();
        response.setStatusCode(connection.getResponseCode());
        response.setBody(responseString.toString());
        return response;
    }

    private void disconnect(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    private void setHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json");
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            connection.setRequestProperty("Username", SecurityContextParser.getEmail());
        }
    }
}