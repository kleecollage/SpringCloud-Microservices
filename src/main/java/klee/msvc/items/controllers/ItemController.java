package klee.msvc.items.controllers;

import klee.msvc.items.models.Item;
import klee.msvc.items.models.ProductDto;
import klee.msvc.items.services.IItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IItemService service;
    private final CircuitBreakerFactory cBreakerFactory;

    public ItemController(@Qualifier("itemServiceWebClient") IItemService service, CircuitBreakerFactory cBreakerFactory) {
        this.service = service;
        this.cBreakerFactory = cBreakerFactory;
    }

    @GetMapping
    public List<Item> list(@RequestParam(name="name", required=false) String name,
                           @RequestHeader(name="token-request", required=false) String token) {
        System.out.println(name);
        System.out.println(token);
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable long id) {
//        Optional<Item> itemOpt = cBreakerFactory.create("items").run(() -> service.findById(id));
        Optional<Item> itemOpt = cBreakerFactory.create("items").run(() -> service.findById(id), e -> {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
            // ALTERNATIVE PATH FOR ERRORS
            ProductDto product = new ProductDto();
            product.setCreatedAt(LocalDate.now());
            product.setId(1L);
            product.setName("Sony Camera");
            product.setPrice(500.50);
            return Optional.of(new Item(product, 5));
        });
        if (itemOpt.isPresent()) {
            return ResponseEntity.ok(itemOpt.get());
        }
        return ResponseEntity.status(404)
                .body(Collections.singletonMap("message", "Product not found in products microservice"));
    }
}
