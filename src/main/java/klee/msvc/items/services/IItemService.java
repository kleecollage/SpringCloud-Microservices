package klee.msvc.items.services;

import klee.msvc.items.models.Item;

import java.util.List;
import java.util.Optional;

public interface IItemService {
    List<Item> findAll();
    Optional<Item> findById(long id);
}

