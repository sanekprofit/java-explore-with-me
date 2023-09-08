package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;

import java.util.Map;

@Service
public class StatClient extends BaseClient {
    private static final String API_PREFIX = "/stats";

    @Autowired
    public StatClient(@Value("${stats-dto.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveRequest(StatDto statDto) {
        return post("/hit", statDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, String[] uris, Boolean unique) {
        Map<String, Object> param = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get("", param);
    }
}
