package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BaseClient {
    private final RestTemplate rest;

    private final String serverUrl;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BaseClient(@Value("${ewm.stat-service.url}") String serverUrl, RestTemplateBuilder builder) {
        this.serverUrl = serverUrl;
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void saveHit(HitDto hit) {
        postHitRequest(hit);
    }

    public List<HitResponseDto> getStats(LocalDateTime start,
                                         LocalDateTime end,
                                         List<String> uris,
                                         Boolean unique) {
        Map<String, Object> param = Map.of(
                "start", start.format(FORMATTER),
                "end", end.format(FORMATTER),
                "unique", unique
        );
        return getStatsRequest(param, uris);
    }

    private <T> void postHitRequest(@Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, null);

        ResponseEntity<Object> statResponse;

        try {
            statResponse = rest.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            log.error("Не удалось сохранить запрос.");
        }
    }

    private <T> List<HitResponseDto> getStatsRequest(@Nullable Map<String, Object> parameters, List<String> uris) {
        ResponseEntity<Object[]> statResponse;

        try {
            if (parameters != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                String serverPath = serverUrl + "/stats?start={start}&end={end}&unique={unique}&"
                        + uris.stream()
                        .map(uri -> "uris=" + uri).collect(Collectors.joining("&"));
                statResponse = rest.getForEntity(serverPath, Object[].class, parameters);
                Object[] obj = statResponse.getBody();

                return Arrays.stream(obj)
                        .map(o -> objectMapper.convertValue(o, HitResponseDto.class))
                        .collect(Collectors.toList());
            }
        } catch (HttpStatusCodeException e) {
            log.error("Не удалось получить статистику.");
        }
        return List.of();
    }
}
