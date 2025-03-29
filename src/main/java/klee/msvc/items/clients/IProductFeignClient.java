package klee.msvc.items.clients;

import klee.msvc.libscommons.entities.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-products", path = "/api/products")
public interface IProductFeignClient {
    @GetMapping
    List<Product> findAll();

    @GetMapping("/{id}")
    Product details(@PathVariable Long id);

    @PostMapping
    public Product create(@RequestBody Product product);

    @PutMapping("/{id}")
    Product update(@PathVariable Long id, @RequestBody Product product);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);
}
