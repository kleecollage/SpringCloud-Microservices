package klee.msvc.products.services;

import klee.msvc.libscommons.entities.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    List<Product> findAll();
    Optional<Product> findById(long id);
    Product save(Product product);
    void deleteById(long id);
}
