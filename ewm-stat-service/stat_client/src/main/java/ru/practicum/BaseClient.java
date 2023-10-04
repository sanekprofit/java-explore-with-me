package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
public class BaseClient {
    private final RestTemplate rest;

    private final String serverUrl;

    public BaseClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder, String serverUrl1) {
        this.serverUrl = serverUrl1;
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void saveHit(HitDto hit) {
        postHitRequest("/hit", hit);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start,
                         LocalDateTime end,
                         String[] uris,
                         Boolean unique) {
        Map<String, Object> param = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return getStatsRequest("/stats", param);
    }

    private <T> ResponseEntity<Object> postHitRequest(String path, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, null);

        ResponseEntity<Object> statResponse;

        try {
            statResponse = rest.exchange(path, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            log.warn("Не удалось сохранить запрос.");
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return prepareGatewayResponse(statResponse);
    }

    private <T> ResponseEntity<Object> getStatsRequest(String path, @Nullable Map<String, Object> parameters) {
        HttpEntity<T> requestEntity = new HttpEntity<>(null, null);

        ResponseEntity<Object> statResponse;

        try {
            if (parameters != null) {
                statResponse = rest.exchange(path, HttpMethod.GET, requestEntity, Object.class, parameters);
            } else {
                statResponse = rest.exchange(path, HttpMethod.GET, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            log.warn("Не удалось получить статистику.");
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return prepareGatewayResponse(statResponse);
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
