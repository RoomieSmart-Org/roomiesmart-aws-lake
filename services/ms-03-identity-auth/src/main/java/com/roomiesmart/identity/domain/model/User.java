package com.roomiesmart.identity.domain.model;

import java.time.Instant;

public record User(
        String id,
        String email,
        String passwordHash,
        String fullName,
        Role role,
        Instant createdAt
) {}