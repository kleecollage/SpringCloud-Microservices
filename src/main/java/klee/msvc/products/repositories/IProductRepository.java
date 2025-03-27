package klee.msvc.products.repositories;

import klee.msvc.products.entities.Product;
import org.springframework.data.repository.CrudRepository;

public interface IProductRepository extends CrudRepository<Product, Long> {

}
