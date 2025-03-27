package klee.msvc.items.controllers;

import klee.msvc.items.models.Item;
import klee.msvc.items.services.IItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final IItemService service;
    public ItemController(IItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<Item> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable long id) {
        Optional<Item> itemOpt = service.findById(id);
        if (itemOpt.isPresent()) {
            return ResponseEntity.ok(itemOpt.get());
        }
        return ResponseEntity.status(404)
                .body(Collections.singletonMap("message", "Product not found in products microservice"));
    }
}
