package klee.msvc.items.clients;

import klee.msvc.items.models.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-products", path = "/api/products")
public interface IProductFeignClient {
    @GetMapping
    List<ProductDto> findAll();

    @GetMapping("/{id}")
    ProductDto details(@PathVariable Long id);

    @PostMapping
    public ProductDto create(@RequestBody ProductDto product);

    @PutMapping("/{id}")
    ProductDto update(@PathVariable Long id, @RequestBody ProductDto product);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);
}
