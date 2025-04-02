package klee.msvc.users.controllers;

import jakarta.validation.Valid;
import klee.msvc.users.entities.User;
import klee.msvc.users.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> findAll() {
        logger.info("UserController::findAll. Fetching all users");
        Iterable<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        logger.info("UserController::findById. Getting user with id: {}", id);
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        logger.info("UserController::findByUsername. Login with username {}", username);
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> findByEmail(@PathVariable String email) {
        logger.info("UserController::findByEmail. Login with email {}", email);
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        logger.info("UserController::create: Creating {}", user);
        if (userService.existsByUsername(user.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        if (userService.existsByEmail(user.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        User savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        logger.info("UserController::update: Updating {}", user);
        if (userService.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        user.setId(id);
        User updatedUser = userService.update(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("UserController::delete: Deleting user with ID {}", id);
        if (userService.findById(id).isEmpty())
            return ResponseEntity.notFound().build();

        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
