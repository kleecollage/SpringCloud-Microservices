package klee.msvc.items.services;

import klee.msvc.items.models.Item;
import klee.msvc.libscommons.entities.Product;

import java.util.List;
import java.util.Optional;

public interface IItemService {
    List<Item> findAll();
    Optional<Item> findById(long id);
    Product save(Product product);
    Product update(Long id, Product product);
    void delete(Long id);
}

