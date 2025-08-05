package com.MagicTheGathering.user.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        java.util.Set<String> roles
) {
}
