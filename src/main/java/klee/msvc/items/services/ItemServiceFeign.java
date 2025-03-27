package klee.msvc.items.services;

import klee.msvc.items.clients.IProductFeignClient;
import klee.msvc.items.models.Item;
import klee.msvc.items.models.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ItemServiceFeign implements IItemService{

    @Autowired
    private IProductFeignClient client;

    @Override
    public List<Item> findAll() {
        return client.findAll()
                .stream()
                .map(product -> new Item(product, new Random().nextInt(10)+1))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(long id) {
        ProductDto product = client.details(id);
        if (product == null) return Optional.empty();
        return Optional.of(new Item(product, new Random().nextInt(10)+1));
    }
}
