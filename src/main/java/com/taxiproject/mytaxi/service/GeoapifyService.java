package com.taxiproject.mytaxi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeoapifyService {

    @Value("${geoapify.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> reverseGeocodeBatch(List<double[]> coordinates) {
        Map<String, String> results = new HashMap<>();

        try {
            List<List<Double>> inputs = new ArrayList<>();
            for (double[] coord : coordinates) {
                inputs.add(List.of(coord[1], coord[0]));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<List<Double>>> request = new HttpEntity<>(inputs, headers);

            String createUrl = "https://api.geoapify.com/v1/batch/geocode/reverse?apiKey=" + apiKey;

            ResponseEntity<String> createResponse = restTemplate.postForEntity(createUrl, request, String.class);

            if (createResponse.getStatusCode() != HttpStatus.ACCEPTED) {
                System.err.println("Geoapify: не удалось создать задачу. Статус: " + createResponse.getStatusCode());
                return results;
            }

            JsonNode createJson = objectMapper.readTree(createResponse.getBody());
            String jobId = createJson.get("id").asText();
            System.out.println("Задача создана, jobId: " + jobId);

            String resultUrl = "https://api.geoapify.com/v1/batch?id=" + jobId + "&apiKey=" + apiKey;

                for (int attempt = 0; attempt < 30; attempt++) {
                    Thread.sleep(2000);
                    ResponseEntity<String> resultResponse = restTemplate.getForEntity(resultUrl, String.class);

                    if (resultResponse.getStatusCode() == HttpStatus.OK) {
                        System.out.println("Geoapify result: " + resultResponse.getBody().substring(0, Math.min(200, resultResponse.getBody().length())));
                        return parseBatchResult(resultResponse.getBody());
                    } else if (resultResponse.getStatusCode() == HttpStatus.ACCEPTED) {
                        System.out.println("Задача ещё выполняется, попытка " + (attempt + 1));
                } else {
                        System.err.println("Ошибка при получении результата: " + resultResponse.getStatusCode());
                        break;
                    }
            }
        } catch (Exception e) {
            System.err.println("Geoapify error: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    private Map<String, String> parseBatchResult(String responseBody) throws Exception {
        Map<String, String> results = new HashMap<>();
        JsonNode root = objectMapper.readTree(responseBody);

        JsonNode resultsNode = root.get("results");

        if (resultsNode != null && resultsNode.isArray()) {
            for (int i = 0; i < resultsNode.size(); i++) {
                JsonNode item = resultsNode.get(i);

                JsonNode resultNode = item.get("result");
                if (resultNode != null) {
                    JsonNode features = resultNode.get("features");
                    if (features != null && features.isArray() && features.size() > 0) {
                        JsonNode properties = features.get(0).get("properties");
                        if (properties != null && properties.has("formatted")) {
                            results.put(String.valueOf(i), properties.get("formatted").asText());
                            continue;
                        }
                    }
                }
                results.put(String.valueOf(i), "Адрес не найден");
            }
        } else {
            System.err.println("Geoapify: поле 'results' не найдено или не является массивом");
        }

        return results;
    }

    public double[] geocodeAddress(String address) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString("https://api.geoapify.com/v1/geocode/search")
                    .queryParam("text", address)
                    .queryParam("lang", "ru")
                    .queryParam("limit", "1")
                    .queryParam("apiKey", apiKey)
                    .build()
                    .toUriString();

            System.out.println("Geoapify geocode URL: " + url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode features = root.get("features");

            if (features != null && features.size() > 0) {
                JsonNode geometry = features.get(0).get("geometry");
                JsonNode coordinates = geometry.get("coordinates");
                return new double[]{
                        coordinates.get(1).asDouble(),
                        coordinates.get(0).asDouble()
                };
            }
        } catch (Exception e) {
            System.err.println("Geocode error: " + e.getMessage());
        }
        return null;
    }
}
