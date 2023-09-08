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
public class DtoClient extends BaseClient {
    private static final String API_PREFIX = "/stats";

    @Autowired
    public DtoClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveRequest(Dto dto) {
        return post("/hit", dto);
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
