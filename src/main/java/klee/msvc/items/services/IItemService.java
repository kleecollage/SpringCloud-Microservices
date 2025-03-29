package klee.msvc.items.services;

import klee.msvc.items.models.Item;
import klee.msvc.items.models.ProductDto;

import java.util.List;
import java.util.Optional;

public interface IItemService {
    List<Item> findAll();
    Optional<Item> findById(long id);
    ProductDto save(ProductDto product);
    ProductDto update(Long id, ProductDto product);
    void delete(Long id);
}

