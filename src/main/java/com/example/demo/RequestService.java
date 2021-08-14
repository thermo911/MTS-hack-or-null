package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;

@org.springframework.stereotype.Service
public class RequestService {

    private final String MlServer = "http://localhost:5000";

    public String getResultOne(String url) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "get_result";
        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        String.format("%s/%s/%s", MlServer, endpoint, url),
                        String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return null;
    }

    public String getResultAll(String sourceFilename) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "get_result_csv";
        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        String.format("%s/%s/%s", MlServer, endpoint, sourceFilename),
                        String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return null;
    }
}
