package klee.msvc.items.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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
import java.util.concurrent.CompletableFuture;

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

    @CircuitBreaker(name ="items", fallbackMethod = "getFallBackMethodProduct") /* This decorator only works on the .yml file */
    @GetMapping("/details/{id}")
    public ResponseEntity<?> details2(@PathVariable long id) {
        Optional<Item> itemOpt = service.findById(id);
        if (itemOpt.isPresent())
            return ResponseEntity.ok(itemOpt.get());

        return ResponseEntity.status(404)
                .body(Collections.singletonMap("message", "Product not found in products microservice"));
    }

    /* TimeLimiter only handle alternatives in timeouts, do not open the circuit breaker */
    /* if you want to combine both, @CircuitBreaker must be declared */
    @CircuitBreaker(name = "items", fallbackMethod = "getFallBackMethodProduct2")
    @TimeLimiter(name ="items") /* This decorator only works on the .yml file */
    @GetMapping("/details2/{id}")
    public CompletableFuture<?> details3(@PathVariable long id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Item> itemOpt = service.findById(id);
            if (itemOpt.isPresent())
                return ResponseEntity.ok(itemOpt.get());

            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("message", "Product not found in products microservice"));
        });
    }

    public ResponseEntity<?> getFallBackMethodProduct(Throwable e) {
        System.out.println(e.getMessage());
        logger.error(e.getMessage());
        // ALTERNATIVE FOR ERRORS
        ProductDto product = new ProductDto();
        product.setCreatedAt(LocalDate.now());
        product.setId(1L);
        product.setName("Sony Camera");
        product.setPrice(500.50);
        return ResponseEntity.ok(new Item(product, 5));
    }

    public CompletableFuture<?> getFallBackMethodProduct2(Throwable e) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
            // ALTERNATIVE FOR ERRORS
            ProductDto product = new ProductDto();
            product.setCreatedAt(LocalDate.now());
            product.setId(1L);
            product.setName("Sony Camera");
            product.setPrice(500.50);
            return ResponseEntity.ok(new Item(product, 5));
        });
    }
}
