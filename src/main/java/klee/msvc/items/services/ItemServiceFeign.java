package klee.msvc.items.services;

import feign.FeignException;
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
        try {
            ProductDto product = client.details(id);
            return Optional.of(new Item(product, new Random().nextInt(10)+1));
        } catch (FeignException e) {
            return Optional.empty();
        }
    }


    @Override
    public ProductDto save(ProductDto product) {
        return null;
    }

    @Override
    public ProductDto update(Long id, ProductDto product) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
