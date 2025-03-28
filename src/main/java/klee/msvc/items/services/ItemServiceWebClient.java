package klee.msvc.items.services;

import klee.msvc.items.models.Item;
import klee.msvc.items.models.ProductDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

// @Primary
@Service
public class ItemServiceWebClient implements IItemService {

    private final WebClient.Builder client;
    public ItemServiceWebClient(WebClient.Builder builder) {
        this.client = builder;
    }

    @Override
    public List<Item> findAll() {
        return this.client.build()
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(ProductDto.class)
                .map(product -> new Item(product, new Random().nextInt(10)+1))
                .collectList()
                .block();
    }

    @Override
    public Optional<Item> findById(long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
//        try {
            return Optional.ofNullable(client.build()
                    .get()
                    .uri("/{id}", params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(ProductDto.class)
                    .map(product -> new Item(product, new Random().nextInt(10)+1))
                    .block());
//        } catch (WebClientResponseException e) {
//            return Optional.empty();
//        }
    }
}














