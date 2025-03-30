package klee.msvc.users.services;

import klee.msvc.users.entities.User;

import java.util.Optional;

public interface IUserService {
    Iterable<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    User update(Long id, User user);
    void deleteById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
