package com.roomiesmart.identity.domain.model;

public record TokenClaims(
        String userId,
        String email,
        Role role,
        boolean valid
) {}