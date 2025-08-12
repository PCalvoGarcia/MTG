package com.MagicTheGathering.user.dto.ADMIN;

public record UserRequestUpdateAdmin(
        String username,
        String email,
        String password,
        com.MagicTheGathering.role.Role role
) {
}
