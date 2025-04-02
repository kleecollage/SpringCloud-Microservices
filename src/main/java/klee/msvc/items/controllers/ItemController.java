package klee.msvc.items.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import klee.msvc.items.models.Item;
import klee.msvc.items.services.IItemService;
import klee.msvc.libscommons.entities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RefreshScope
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IItemService service;
    private final CircuitBreakerFactory cBreakerFactory;

    /* This value is fetched from our config file in "$HOME/Documents/JavaProjects/Microservices/config"*/
    @Value("${configuration.text}")
    private String text;

    @Autowired
    private Environment env;

    public ItemController(@Qualifier("itemServiceFeign") IItemService service,
                          CircuitBreakerFactory cBreakerFactory) {
        this.service = service;
        this.cBreakerFactory = cBreakerFactory;
    }

    /* Config values can be injected directly in the mapping */
    @GetMapping("/fetch-configs")
    public ResponseEntity<?> fetchConfig(@Value("${server.port}") String port) {
        Map<String, String> json = new HashMap<>();
        json.put("text", text);
        json.put("port", port);
        logger.info(port);
        logger.info(text);

        if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
            json.put("author.name", env.getProperty("configuration.author.name"));
            json.put("author.email", env.getProperty("configuration.author.email"));
        }
        return ResponseEntity.ok(json);
    }

    @GetMapping
    public List<Item> list(@RequestParam(name="name", required=false) String name,
                           @RequestHeader(name="token-request", required=false) String token) {
        System.out.println(name);
        System.out.println(token);
        logger.info("Call to method ItemController::list()");
        logger.info("Request Parameter: {}", name);
        logger.info("Token: {}", token);
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable long id) {
//        Optional<Item> itemOpt = cBreakerFactory.create("items").run(() -> service.findById(id));
        Optional<Item> itemOpt = cBreakerFactory.create("items").run(() -> service.findById(id), e -> {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
            // ALTERNATIVE PATH FOR ERRORS
            Product product = new Product();
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        logger.info("Creating Product: {}", product);
        return service.save(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        logger.info("Updating Product: {}", product);
        return service.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        logger.info("Deleting Product: {}", id);
        service.delete(id);
    }

    public ResponseEntity<?> getFallBackMethodProduct(Throwable e) {
        System.out.println(e.getMessage());
        logger.error(e.getMessage());
        // ALTERNATIVE FOR ERRORS
        Product product = new Product();
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
            Product product = new Product();
            product.setCreatedAt(LocalDate.now());
            product.setId(1L);
            product.setName("Sony Camera");
            product.setPrice(500.50);
            return ResponseEntity.ok(new Item(product, 5));
        });
    }
}
