package klee.msvc.items.clients;

import klee.msvc.items.models.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(url = "localhost:8000")
public interface IProductFeignClient {
    @GetMapping
    List<ProductDto> findAll();

    @GetMapping("/{id}")
    ProductDto details(@PathVariable long id);
}
