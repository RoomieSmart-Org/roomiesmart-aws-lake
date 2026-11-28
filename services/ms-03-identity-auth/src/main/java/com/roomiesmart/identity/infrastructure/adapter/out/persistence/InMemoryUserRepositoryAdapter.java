package com.roomiesmart.identity.infrastructure.adapter.out.persistence;

import com.roomiesmart.identity.domain.model.User;
import com.roomiesmart.identity.domain.port.out.UserRepositoryPort;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepositoryAdapter implements UserRepositoryPort {

    private final ConcurrentHashMap<String, User> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        usersByEmail.put(user.email(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email);
    }
}
