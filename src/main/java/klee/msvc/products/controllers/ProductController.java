package klee.msvc.products.controllers;

import klee.msvc.libscommons.entities.Product;
import klee.msvc.products.services.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IProductService service;

    public ProductController(IProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> list(@RequestHeader(name = "message-request", required = false) String message) {
        logger.info("Enter into ProductController::list()");
        logger.info("message: {}", message);
        return this.service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) throws InterruptedException {
        logger.info("Enter into ProductController::detail()");
        if (id.equals(10L))
            throw new IllegalStateException("Product not found");

        if (id.equals(7L))
            TimeUnit.SECONDS.sleep(3L);

        Optional<Product> productOptional = this.service.findById(id);
        if (productOptional.isPresent())
            return ResponseEntity.ok(productOptional.orElseThrow());

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        logger.info("Enter into ProductController::create(), creating: {}", product);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(product));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product) {
        logger.info("Enter into ProductController::update(), updating: {}", product);
        Optional<Product> productOptional = service.findById(id);
        if (productOptional.isPresent()) {
            Product productUpdate = productOptional.orElseThrow();
            productUpdate.setName(product.getName());
            productUpdate.setPrice(product.getPrice());
            productUpdate.setCreatedAt(product.getCreatedAt());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(productUpdate));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Product> productOptional = service.findById(id);
        if (productOptional.isPresent()) {
            this.service.deleteById(id);
            logger.info("Enter into ProductController::delete(), deleting: {}", productOptional.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

















