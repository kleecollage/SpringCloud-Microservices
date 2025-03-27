package klee.msvc.products.services;

import klee.msvc.products.entities.Product;
import klee.msvc.products.repositories.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    final private IProductRepository repository;
    final private Environment env;
    ProductServiceImpl(IProductRepository repository, Environment env) {
        this.env = env;
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return ((List<Product>) repository.findAll()).stream().map(product -> {
            product.setPort((Objects.requireNonNull(env.getProperty("local.server.port", Integer.class))));
            return product;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(long id) {
        return repository.findById(id).map(product -> {
            product.setPort((Objects.requireNonNull(env.getProperty("local.server.port", Integer.class))));
            return product;
        });
    }
}
