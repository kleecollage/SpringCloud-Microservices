package klee.msvc.items.services;

import klee.msvc.items.models.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceWebClient implements IItemService {

    private final WebClient.Builder client;
    public ItemServiceWebClient(WebClient.Builder builder) {
        this.client = builder;
    }

    @Override
    public List<Item> findAll() {
        return List.of();
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.empty();
    }
}
