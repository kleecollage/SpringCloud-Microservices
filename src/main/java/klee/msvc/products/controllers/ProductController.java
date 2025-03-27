package klee.msvc.products.controllers;

import klee.msvc.products.entities.Product;
import klee.msvc.products.services.IProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    final private IProductService service;
    public ProductController(IProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> list() {
        return this.service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable long id) {
        Optional<Product> productOptional = this.service.findById(id);
        if (productOptional.isPresent()) {
            return ResponseEntity.ok(productOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }
}
