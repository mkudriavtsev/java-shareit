package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long ownerId, CreateItemDto dto) {
        return post("", ownerId, dto);
    }

    public ResponseEntity<Object> patch(long id, long ownerId, PatchItemDto dto) {
        return patch("/" + id, ownerId, dto);
    }

    public ResponseEntity<Object> getById(long id, long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getByOwnerId(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItems(long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text);
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(long id, long userId, CommentDto commentDto) {
        return post("/" + id + "/comment", userId, commentDto);
    }
}
