package com.roomiesmart.identity.domain.port.out;

import com.roomiesmart.identity.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}